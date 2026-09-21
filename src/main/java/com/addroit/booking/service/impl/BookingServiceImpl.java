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
import com.addroit.booking.exception.BookingAlreadyCancelledException;
import com.addroit.booking.exception.BookingNotFoundException;
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

        // Get the currently logged-in attendee using email from JWT
        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        // Only attendees are allowed to book tickets
        if (attendee.getRole() != Role.ATTENDEE) {
            throw new ForbiddenOperationException(
                    BookingConstants.MESSAGE_403
            );
        }

        // Lock the event row to prevent ticket overselling during concurrent booking
        Event event = eventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                EventConstants.MESSAGE_404
                        )
                );

        // Check ticket availability after getting the event lock
        if (event.getAvailableTickets()
                < requestDto.getQuantity()) {

            throw new InsufficientTicketsException(
                    BookingConstants.MESSAGE_SOLD_OUT
            );
        }

        // Reduce available tickets after successful booking validation
        event.setAvailableTickets(
                event.getAvailableTickets()
                        - requestDto.getQuantity()
        );

        // Create a confirmed booking linked to the selected event and attendee
        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .event(event)
                .attendee(attendee)
                .quantity(requestDto.getQuantity())
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();

        // Save the booking; event ticket update is saved automatically at transaction commit
        Booking savedBooking =
                bookingRepository.save(booking);

        return BookingMapper.toResponseDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMyBookings(
            String attendeeEmail) {

        // Get logged-in attendee to fetch only their own bookings
        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        // Return bookings in latest-booked-first order
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

        // Get the currently logged-in attendee using email from JWT
        User attendee = userRepository
                .findByEmail(attendeeEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        // Lock booking and verify it belongs to the logged-in attendee
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

        // Prevent the same booking from being cancelled more than once
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingAlreadyCancelledException(
                    BookingConstants.MESSAGE_ALREADY_CANCELLED
            );
        }

        // Lock the event because cancellation changes available ticket count
        Event event = eventRepository
                .findByIdForUpdate(booking.getEvent().getId())
                .orElseThrow(() ->
                        new EventNotFoundException(
                                EventConstants.MESSAGE_404
                        )
                );

        // Mark booking as cancelled and store cancellation time
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        // Add cancelled ticket quantity back to available ticket capacity
        event.setAvailableTickets(
                event.getAvailableTickets()
                        + booking.getQuantity()
        );

        return BookingMapper.toResponseDto(booking);
    }

    private String generateBookingReference() {

        // Generate a short unique value for the user-friendly booking reference
        String randomValue = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);

        return BookingConstants.BOOKING_REFERENCE_PREFIX
                + randomValue;
    }
}