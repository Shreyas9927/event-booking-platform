import axios from "axios";

// Use common settings for all requests sent through this client
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use(
    (config) => {
        const accessToken =
            localStorage.getItem("accessToken");

        // Attach the saved JWT before sending the request
        if (accessToken) {
            config.headers.Authorization =
                `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => Promise.reject(error),
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const statusCode = error.response?.status;
        const accessToken =
            localStorage.getItem("accessToken");

        // Clear saved login data when a request returns 401 and a token exists
        if (statusCode === 401 && accessToken) {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("user");

            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }

        // Pass the error back so the calling page can handle it
        return Promise.reject(error);
    },
);

export default api;