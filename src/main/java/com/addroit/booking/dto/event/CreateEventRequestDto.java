package com.addroit.booking.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDto {

    @NotBlank(message = "Event name is required")
    @Size(
            min = 3,
            max = 150,
            message = "Event name must contain between 3 and 150 characters"
    )
    private String name;

    @NotBlank(message = "Event description is required")
    @Size(
            max = 2000,
            message = "Description must not exceed 2000 characters"
    )
    private String description;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotNull(message = "Capacity is required")
    @Min(
            value = 1,
            message = "Capacity must be at least 1"
    )
    private Integer capacity;
}