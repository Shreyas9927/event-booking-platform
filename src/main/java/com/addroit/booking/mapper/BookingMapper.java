package com.addroit.booking.mapper;

import com.addroit.booking.dto.booking.BookingResponseDto;
import com.addroit.booking.entity.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponseDto toResponseDto(Booking booking) {

        return BookingResponseDto.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .eventName(booking.getEvent().getName())
                .eventDate(booking.getEvent().getEventDate())
                .quantity(booking.getQuantity())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}