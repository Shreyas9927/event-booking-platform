package com.addroit.booking;

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
import com.addroit.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingConcurrencyIntegrationTest {

    private static final int EVENT_CAPACITY = 5;
    private static final int BOOKING_ATTEMPTS = 20;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long eventId;

    private final List<String> attendeeEmails =
            new ArrayList<>();

    @BeforeEach
    void setUp() {

        bookingRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
        attendeeEmails.clear();

        User organiser = User.builder()
                .name("Test Organiser")
                .email("organiser@test.com")
                .passwordHash("not-used")
                .role(Role.ORGANISER)
                .build();

        organiser = userRepository.save(organiser);

        Event event = Event.builder()
                .name("Concurrency Test Event")
                .description("Verify no overselling")
                .eventDate(LocalDateTime.now().plusDays(10))
                .capacity(EVENT_CAPACITY)
                .availableTickets(EVENT_CAPACITY)
                .organiser(organiser)
                .build();

        eventId = eventRepository.save(event).getId();

        for (int index = 1;
             index <= BOOKING_ATTEMPTS;
             index++) {

            String email =
                    "attendee" + index + "@test.com";

            User attendee = User.builder()
                    .name("Attendee " + index)
                    .email(email)
                    .passwordHash("not-used")
                    .role(Role.ATTENDEE)
                    .build();

            userRepository.save(attendee);
            attendeeEmails.add(email);
        }
    }

    @Test
    void shouldNeverOversellWhenManyUsersBookAtOnce()
            throws InterruptedException {

        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        BOOKING_ATTEMPTS
                );

        CountDownLatch readyLatch =
                new CountDownLatch(BOOKING_ATTEMPTS);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        CountDownLatch completedLatch =
                new CountDownLatch(BOOKING_ATTEMPTS);

        AtomicInteger successfulBookings =
                new AtomicInteger();

        AtomicInteger rejectedBookings =
                new AtomicInteger();

        AtomicReference<Throwable> unexpectedError =
                new AtomicReference<>();

        CreateBookingRequestDto requestDto =
                new CreateBookingRequestDto(1);

        for (String attendeeEmail : attendeeEmails) {

            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    bookingService.bookTickets(
                            eventId,
                            requestDto,
                            attendeeEmail
                    );

                    successfulBookings.incrementAndGet();

                } catch (InsufficientTicketsException exception) {
                    rejectedBookings.incrementAndGet();

                } catch (Throwable throwable) {
                    unexpectedError.compareAndSet(
                            null,
                            throwable
                    );

                } finally {
                    completedLatch.countDown();
                }
            });
        }

        assertTrue(
                readyLatch.await(
                        10,
                        TimeUnit.SECONDS
                ),
                "Requests were not ready in time"
        );

        startLatch.countDown();

        assertTrue(
                completedLatch.await(
                        30,
                        TimeUnit.SECONDS
                ),
                "Requests did not complete in time"
        );

        executorService.shutdown();

        if (unexpectedError.get() != null) {
            fail(
                    "Unexpected error: "
                            + unexpectedError.get()
                            .getMessage()
            );
        }

        Event updatedEvent = eventRepository
                .findById(eventId)
                .orElseThrow();

        int confirmedTicketCount =
                bookingRepository.findAll()
                        .stream()
                        .filter(booking ->
                                booking.getStatus()
                                        == BookingStatus.CONFIRMED
                        )
                        .mapToInt(Booking::getQuantity)
                        .sum();

        assertEquals(
                EVENT_CAPACITY,
                successfulBookings.get()
        );

        assertEquals(
                BOOKING_ATTEMPTS - EVENT_CAPACITY,
                rejectedBookings.get()
        );

        assertEquals(
                0,
                updatedEvent.getAvailableTickets()
        );

        assertEquals(
                EVENT_CAPACITY,
                confirmedTicketCount
        );
    }
}