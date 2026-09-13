package com.addroit.booking.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequestDto {

    @NotNull(message = "Ticket quantity is required")
    @Min(
            value = 1,
            message = "Ticket quantity must be at least 1"
    )
    private Integer quantity;
}