package com.addroit.booking.mapper;

import com.addroit.booking.dto.event.CreateEventRequestDto;
import com.addroit.booking.dto.event.EventResponseDto;
import com.addroit.booking.entity.Event;
import com.addroit.booking.entity.User;

public final class EventMapper {

    private EventMapper() {
    }

    public static Event toEntity(
            CreateEventRequestDto requestDto,
            User organiser) {

        // Set available tickets equal to total capacity when event is created
        return Event.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .eventDate(requestDto.getEventDate())
                .capacity(requestDto.getCapacity())
                .availableTickets(requestDto.getCapacity())
                .organiser(organiser)
                .build();
    }

    public static EventResponseDto toResponseDto(Event event) {

        return EventResponseDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .availableTickets(event.getAvailableTickets())

                // Calculate booked tickets to avoid storing duplicate ticket data
                .bookedTickets(
                        event.getCapacity() - event.getAvailableTickets()
                )
                .organiserId(event.getOrganiser().getId())
                .organiserName(event.getOrganiser().getName())
                .createdAt(event.getCreatedAt())
                .build();
    }
}