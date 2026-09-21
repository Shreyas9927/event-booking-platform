import api from "./api";

// Book selected tickets for a specific event
export const bookTickets = async (
    eventId,
    quantity,
) => {
    const response = await api.post(
        `/events/${eventId}/bookings`,
        { quantity },
    );

    return response.data.object;
};

// Fetch bookings belonging to the logged-in attendee
export const getMyBookings = async () => {
    const response = await api.get("/bookings/me");
    return response.data.object;
};

// Cancel one booking belonging to the logged-in attendee
export const cancelBooking = async (bookingId) => {
    const response = await api.patch(
        `/bookings/${bookingId}/cancel`,
    );

    return response.data.object;
};