package com.addroit.booking.constants;

public final class BookingConstants {

    private BookingConstants() {
    }

    // Create booking
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 =
            "Tickets booked successfully";

    // Fetch or cancel booking
    public static final String STATUS_200 = "200";
    public static final String MESSAGE_FETCHED =
            "Bookings fetched successfully";
    public static final String MESSAGE_CANCELLED =
            "Booking cancelled successfully";

    // Bad request
    public static final String STATUS_400 = "400";
    public static final String MESSAGE_400 =
            "Ticket quantity must be greater than zero";

    // Forbidden
    public static final String STATUS_403 = "403";
    public static final String MESSAGE_403 =
            "You cannot access or cancel another user's booking";

    // Not found
    public static final String STATUS_404 = "404";
    public static final String MESSAGE_404 =
            "Booking not found";

    // Conflict
    public static final String STATUS_409 = "409";
    public static final String MESSAGE_SOLD_OUT =
            "Requested number of tickets is not available";
    public static final String MESSAGE_ALREADY_CANCELLED =
            "Booking is already cancelled";

    public static final String BOOKING_REFERENCE_PREFIX =
            "BKG-";
}