package com.addroit.booking.service;

import com.addroit.booking.dto.event.CreateEventRequestDto;
import com.addroit.booking.dto.event.EventResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(@Valid CreateEventRequestDto requestDto, String name);

    List<EventResponseDto> getUpcomingEvents();

    List<EventResponseDto> getMyEvents(String name);
}
