package com.addroit.booking.service.impl;

import com.addroit.booking.constants.AuthConstants;
import com.addroit.booking.constants.EventConstants;
import com.addroit.booking.dto.event.CreateEventRequestDto;
import com.addroit.booking.dto.event.EventResponseDto;
import com.addroit.booking.entity.Event;
import com.addroit.booking.entity.User;
import com.addroit.booking.enums.Role;
import com.addroit.booking.exception.ForbiddenOperationException;
import com.addroit.booking.exception.UserNotFoundException;
import com.addroit.booking.mapper.EventMapper;
import com.addroit.booking.repository.EventRepository;
import com.addroit.booking.repository.UserRepository;
import com.addroit.booking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventResponseDto createEvent(
            CreateEventRequestDto requestDto,
            String organiserEmail) {

        // Get the currently logged-in organiser using email from JWT
        User organiser = userRepository
                .findByEmail(organiserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        // Only organisers are allowed to create events
        if (organiser.getRole() != Role.ORGANISER) {
            throw new ForbiddenOperationException(
                    EventConstants.MESSAGE_403
            );
        }

        // Convert validated request data into an Event entity
        // Available tickets are initially set equal to event capacity
        Event event = EventMapper.toEntity(
                requestDto,
                organiser
        );

        // Save event along with organiser relationship in database
        Event savedEvent = eventRepository.save(event);

        return EventMapper.toResponseDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getUpcomingEvents() {

        // Fetch only future events and show nearest event first
        return eventRepository
                .findByEventDateAfterOrderByEventDateAsc(
                        LocalDateTime.now()
                )
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getMyEvents(
            String organiserEmail) {

        // Get the logged-in organiser using email from JWT
        User organiser = userRepository
                .findByEmail(organiserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                AuthConstants.MESSAGE_404
                        )
                );

        // Fetch only events created by this organiser, latest created first
        return eventRepository
                .findByOrganiserIdOrderByCreatedAtDesc(
                        organiser.getId()
                )
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }
}