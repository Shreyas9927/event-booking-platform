import api from "./api";

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

export const getMyBookings = async () => {
    const response = await api.get("/bookings/me");
    return response.data.object;
};

export const cancelBooking = async (bookingId) => {
    const response = await api.patch(
        `/bookings/${bookingId}/cancel`,
    );

    return response.data.object;
};