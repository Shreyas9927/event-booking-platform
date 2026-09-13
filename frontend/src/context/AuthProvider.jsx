import { useState } from "react";
import AuthContext from "./AuthContext";
import { loginUser } from "../services/authService";

const getStoredUser = () => {
    const storedUser = localStorage.getItem("user");

    if (!storedUser) {
        return null;
    }

    try {
        return JSON.parse(storedUser);
    } catch {
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        return null;
    }
};

function AuthProvider({ children }) {
    const [user, setUser] = useState(getStoredUser);

    const login = async (credentials) => {
        const response = await loginUser(credentials);
        const authenticationData = response.object;

        const loggedInUser = {
            userId: authenticationData.userId,
            name: authenticationData.name,
            email: authenticationData.email,
            role: authenticationData.role,
        };

        localStorage.setItem(
            "accessToken",
            authenticationData.accessToken,
        );

        localStorage.setItem(
            "user",
            JSON.stringify(loggedInUser),
        );

        setUser(loggedInUser);
        return loggedInUser;
    };

    const logout = () => {
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