import api from "./api";

// Send new user details to the registration API
export const registerUser = async (registrationData) => {
    const response = await api.post("/auth/register", registrationData);
    return response.data;
};

// Send login credentials and return the backend response body
export const loginUser = async (loginData) => {
    const response = await api.post("/auth/login", loginData);
    return response.data;
};