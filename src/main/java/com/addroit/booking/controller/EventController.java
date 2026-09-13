package com.addroit.booking.controller;

import com.addroit.booking.constants.EventConstants;
import com.addroit.booking.dto.ResponseDto;
import com.addroit.booking.dto.event.CreateEventRequestDto;
import com.addroit.booking.dto.event.EventResponseDto;
import com.addroit.booking.service.EventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(
        name = "Events",
        description = "Event management APIs"
)
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANISER')")
    @Operation(
            summary = "Create an event",
            description = "Allows an organiser to create an event"
    )
    public ResponseEntity<ResponseDto> createEvent(
            @Valid @RequestBody CreateEventRequestDto requestDto,
            Authentication authentication) {

        EventResponseDto eventResponseDto =
                eventService.createEvent(
                        requestDto,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(
                        EventConstants.STATUS_201,
                        EventConstants.MESSAGE_201,
                        eventResponseDto
                ));
    }

    @GetMapping
    @Operation(
            summary = "Browse upcoming events",
            description = "Returns all upcoming events ordered by event date"
    )
    public ResponseEntity<ResponseDto> getUpcomingEvents() {

        List<EventResponseDto> events =
                eventService.getUpcomingEvents();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        EventConstants.STATUS_200,
                        EventConstants.MESSAGE_200,
                        events
                ));
    }

    @GetMapping("/my-events")
    @PreAuthorize("hasRole('ORGANISER')")
    @Operation(
            summary = "View organiser events",
            description = "Returns events created by the logged-in organiser"
    )
    public ResponseEntity<ResponseDto> getMyEvents(
            Authentication authentication) {

        List<EventResponseDto> events =
                eventService.getMyEvents(
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        EventConstants.STATUS_200,
                        EventConstants.MESSAGE_200,
                        events
                ));
    }
}