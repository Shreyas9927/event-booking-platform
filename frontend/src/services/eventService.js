import api from "./api";

export const getUpcomingEvents = async () => {
    const response = await api.get("/events");
    return response.data.object;
};

export const getMyEvents = async () => {
    const response = await api.get("/events/my-events");
    return response.data.object;
};

export const createEvent = async (eventData) => {
    const response = await api.post("/events", eventData);
    return response.data.object;
};