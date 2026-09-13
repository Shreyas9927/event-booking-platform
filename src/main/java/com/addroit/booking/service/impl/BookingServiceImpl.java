package com.addroit.booking.service.impl;

import com.addroit.booking.constants.AuthConstants;
import com.addroit.booking.constants.BookingConstants;
import com.addroit.booking.constants.EventConstants;
import com.addroit.booking.dto.booking.BookingResponseDto;
import com.addroit.booking.dto.booking.CreateBookingRequestDto;
import com.addroit.booking.entity.Booking;
import com.addroit.booking.entity.Event;
import com.addroit.booking.entity.User;
import com.addroit.booking.enums.BookingStatus;
import com.addroit.booking.enums.Role;
import com.addroit.booking.exception.EventNotFoundException;
import com.addroit.booking.exception.ForbiddenOperationException;
import com.addroit.booking.exception.InsufficientTicketsException;
import com.addroit.booking.exception.UserNotFoundException;
import com.addroit.booking.mapper.BookingMapper;
import com.addroit.booking.repository.BookingRepository;
import com.addroit.booking.repository.EventRepository;
import com.addroit.booking.repository.UserRepository;
import com.addroit.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.addroit.booking.exception.BookingAlreadyCancelledException;
import com.addroit.booking.exception.BookingNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto bookTickets(
            Long eventId,
            CreateBookingRequestDto requestDto,
            String attendeeEmail) {

        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        if (attendee.getRole() != Role.ATTENDEE) {
            throw new ForbiddenOperationException(
                    BookingConstants.MESSAGE_403
            );
        }

        Event event = eventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                EventConstants.MESSAGE_404
                        )
                );

        if (event.getAvailableTickets()
                < requestDto.getQuantity()) {

            throw new InsufficientTicketsException(
                    BookingConstants.MESSAGE_SOLD_OUT
            );
        }

        event.setAvailableTickets(
                event.getAvailableTickets()
                        - requestDto.getQuantity()
        );

        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .event(event)
                .attendee(attendee)
                .quantity(requestDto.getQuantity())
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();

        Booking savedBooking =
                bookingRepository.save(booking);

        return BookingMapper.toResponseDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMyBookings(
            String attendeeEmail) {

        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        return bookingRepository
                .findByAttendeeIdOrderByBookedAtDesc(
                        attendee.getId()
                )
                .stream()
                .map(BookingMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(
            Long bookingId,
            String attendeeEmail) {

        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        Booking booking = bookingRepository
                .findOwnedBookingForUpdate(
                        bookingId,
                        attendee.getId()
                )
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                BookingConstants.MESSAGE_404
                        )
                );

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingAlreadyCancelledException(
                    BookingConstants.MESSAGE_ALREADY_CANCELLED
            );
        }

        Event event = eventRepository
                .findByIdForUpdate(booking.getEvent().getId())
                .orElseThrow(() ->
                        new EventNotFoundException(
                                EventConstants.MESSAGE_404
                        )
                );

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        event.setAvailableTickets(
                event.getAvailableTickets()
                        + booking.getQuantity()
        );

        return BookingMapper.toResponseDto(booking);
    }

    private String generateBookingReference() {

        String randomValue = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);

        return BookingConstants.BOOKING_REFERENCE_PREFIX
                + randomValue;
    }
}