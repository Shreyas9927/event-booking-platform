package com.addroit.booking;

import com.addroit.booking.dto.booking.BookingResponseDto;
import com.addroit.booking.dto.booking.CreateBookingRequestDto;
import com.addroit.booking.entity.Booking;
import com.addroit.booking.entity.Event;
import com.addroit.booking.entity.User;
import com.addroit.booking.enums.BookingStatus;
import com.addroit.booking.enums.Role;
import com.addroit.booking.exception.InsufficientTicketsException;
import com.addroit.booking.repository.BookingRepository;
import com.addroit.booking.repository.EventRepository;
import com.addroit.booking.repository.UserRepository;
import com.addroit.booking.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldBookTicketSuccessfully() {

        User attendee = createAttendee();

        Event event = createEvent(5, 2);

        when(userRepository.findByEmail(attendee.getEmail()))
                .thenReturn(Optional.of(attendee));

        when(eventRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(event));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        BookingResponseDto response =
                bookingService.bookTickets(
                        1L,
                        new CreateBookingRequestDto(1),
                        attendee.getEmail()
                );

        assertEquals(1, event.getAvailableTickets());
        assertEquals(1, response.getQuantity());
        assertEquals(
                BookingStatus.CONFIRMED,
                response.getStatus()
        );

        verify(bookingRepository)
                .save(any(Booking.class));
    }

    @Test
    void shouldRejectBookingWhenTicketsAreInsufficient() {

        User attendee = createAttendee();

        Event event = createEvent(5, 1);

        when(userRepository.findByEmail(attendee.getEmail()))
                .thenReturn(Optional.of(attendee));

        when(eventRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(event));

        assertThrows(
                InsufficientTicketsException.class,
                () -> bookingService.bookTickets(
                        1L,
                        new CreateBookingRequestDto(2),
                        attendee.getEmail()
                )
        );

        assertEquals(1, event.getAvailableTickets());

        verify(
                bookingRepository,
                never()
        ).save(any(Booking.class));
    }

    @Test
    void shouldCancelBookingAndReturnTickets() {

        User attendee = createAttendee();

        Event event = createEvent(5, 3);

        Booking booking = Booking.builder()
                .id(1L)
                .bookingReference("BKG-TEST123")
                .event(event)
                .attendee(attendee)
                .quantity(2)
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(attendee.getEmail()))
                .thenReturn(Optional.of(attendee));

        when(bookingRepository.findOwnedBookingForUpdate(
                1L,
                attendee.getId()
        )).thenReturn(Optional.of(booking));

        when(eventRepository.findByIdForUpdate(event.getId()))
                .thenReturn(Optional.of(event));

        BookingResponseDto response =
                bookingService.cancelBooking(
                        1L,
                        attendee.getEmail()
                );

        assertEquals(
                BookingStatus.CANCELLED,
                response.getStatus()
        );

        assertEquals(5, event.getAvailableTickets());

        assertNotNull(booking.getCancelledAt());
    }

    private User createAttendee() {

        return User.builder()
                .id(1L)
                .name("Test Attendee")
                .email("attendee@test.com")
                .passwordHash("not-used")
                .role(Role.ATTENDEE)
                .build();
    }

    private Event createEvent(
            int capacity,
            int availableTickets) {

        return Event.builder()
                .id(1L)
                .name("Test Event")
                .description("Booking service unit test")
                .eventDate(LocalDateTime.now().plusDays(5))
                .capacity(capacity)
                .availableTickets(availableTickets)
                .build();
    }
}