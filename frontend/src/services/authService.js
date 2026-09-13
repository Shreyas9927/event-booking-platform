import api from "./api";

export const registerUser = async (registrationData) => {
    const response = await api.post("/auth/register", registrationData);
    return response.data;
};

export const loginUser = async (loginData) => {
    const response = await api.post("/auth/login", loginData);
    return response.data;
};