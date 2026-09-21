import { useState } from "react";
import AuthContext from "./AuthContext";
import { loginUser } from "../services/authService";

// Restore saved user details when the application loads
const getStoredUser = () => {
    const storedUser = localStorage.getItem("user");

    if (!storedUser) {
        return null;
    }

    try {
        return JSON.parse(storedUser);
    } catch {
        // Clear saved login data if the user JSON cannot be read
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        return null;
    }
};

function AuthProvider({ children }) {
    const [user, setUser] = useState(getStoredUser);

    const login = async (credentials) => {
        // Send credentials to the backend and read the login response
        const response = await loginUser(credentials);
        const authenticationData = response.object;

        const loggedInUser = {
            userId: authenticationData.userId,
            name: authenticationData.name,
            email: authenticationData.email,
            role: authenticationData.role,
        };

        // Save the token for API requests and user details for page refreshes
        localStorage.setItem(
            "accessToken",
            authenticationData.accessToken,
        );

        localStorage.setItem(
            "user",
            JSON.stringify(loggedInUser),
        );

        // Update React state so the interface reflects the login
        setUser(loggedInUser);
        return loggedInUser;
    };

    const logout = () => {
        // Remove saved login data and reset the current user
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                logout,
                isAuthenticated: Boolean(user),
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export default AuthProvider;