package com.addroit.booking.service;

import com.addroit.booking.dto.booking.BookingResponseDto;
import com.addroit.booking.dto.booking.CreateBookingRequestDto;
import jakarta.validation.Valid;

import java.util.List;

public interface BookingService {
    BookingResponseDto bookTickets(Long eventId, @Valid CreateBookingRequestDto requestDto, String name);

    List<BookingResponseDto> getMyBookings(String name);

    BookingResponseDto cancelBooking(Long bookingId, String name);
}
