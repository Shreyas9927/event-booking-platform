package com.addroit.booking.constants;

public final class EventConstants {

    private EventConstants() {
    }

    // Create event
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 =
            "Event created successfully";

    // Fetch events
    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 =
            "Events fetched successfully";

    // Bad request
    public static final String STATUS_400 = "400";
    public static final String MESSAGE_400 =
            "Invalid event data";

    // Forbidden
    public static final String STATUS_403 = "403";
    public static final String MESSAGE_403 =
            "You are not allowed to perform this event operation";

    // Not found
    public static final String STATUS_404 = "404";
    public static final String MESSAGE_404 =
            "Event not found";
}