package com.addroit.booking.controller;

import com.addroit.booking.constants.BookingConstants;
import com.addroit.booking.dto.ResponseDto;
import com.addroit.booking.dto.booking.BookingResponseDto;
import com.addroit.booking.dto.booking.CreateBookingRequestDto;
import com.addroit.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Bookings",
        description = "Ticket booking and cancellation APIs"
)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/events/{eventId}/bookings")
    @PreAuthorize("hasRole('ATTENDEE')")
    @Operation(
            summary = "Book tickets",
            description = "Allows an attendee to book tickets safely"
    )
    public ResponseEntity<ResponseDto> bookTickets(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateBookingRequestDto requestDto,
            Authentication authentication) {

        BookingResponseDto bookingResponseDto =
                bookingService.bookTickets(
                        eventId,
                        requestDto,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(
                        BookingConstants.STATUS_201,
                        BookingConstants.MESSAGE_201,
                        bookingResponseDto
                ));
    }

    @GetMapping("/bookings/me")
    @PreAuthorize("hasRole('ATTENDEE')")
    @Operation(
            summary = "View my bookings",
            description = "Returns bookings belonging to the logged-in attendee"
    )
    public ResponseEntity<ResponseDto> getMyBookings(
            Authentication authentication) {

        List<BookingResponseDto> bookings =
                bookingService.getMyBookings(
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        BookingConstants.STATUS_200,
                        BookingConstants.MESSAGE_FETCHED,
                        bookings
                ));
    }

    @PatchMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("hasRole('ATTENDEE')")
    @Operation(
            summary = "Cancel a booking",
            description = "Cancels the logged-in attendee's booking and returns tickets"
    )
    public ResponseEntity<ResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        BookingResponseDto bookingResponseDto =
                bookingService.cancelBooking(
                        bookingId,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        BookingConstants.STATUS_200,
                        BookingConstants.MESSAGE_CANCELLED,
                        bookingResponseDto
                ));
    }
}