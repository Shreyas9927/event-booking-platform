import { useContext } from "react";
import AuthContext from "./AuthContext";

const useAuth = () => {
    // Read authentication values from the nearest provider
    const context = useContext(AuthContext);

    // Catch components using this hook outside AuthProvider
    if (!context) {
        throw new Error(
            "useAuth must be used inside AuthProvider",
        );
    }

    return context;
};

export default useAuth;