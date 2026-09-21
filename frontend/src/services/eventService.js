import api from "./api";

// Fetch all upcoming events for attendees
export const getUpcomingEvents = async () => {
    const response = await api.get("/events");
    return response.data.object;
};

// Fetch events created by the logged-in organiser
export const getMyEvents = async () => {
    const response = await api.get("/events/my-events");
    return response.data.object;
};

// Send new event details to the backend
export const createEvent = async (eventData) => {
    const response = await api.post("/events", eventData);
    return response.data.object;
};