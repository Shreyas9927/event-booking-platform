package com.addroit.booking.dto.booking;

import com.addroit.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Long id;

    private String bookingReference;

    private String eventName;

    private LocalDateTime eventDate;

    private Integer quantity;

    private BookingStatus status;

    private LocalDateTime bookedAt;
}