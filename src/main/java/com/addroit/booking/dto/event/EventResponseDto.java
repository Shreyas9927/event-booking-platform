package com.addroit.booking.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime eventDate;

    private Integer capacity;

    private Integer availableTickets;

    private Integer bookedTickets;

    private Long organiserId;

    private String organiserName;

    private LocalDateTime createdAt;
}