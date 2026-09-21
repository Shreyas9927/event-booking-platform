import { createContext } from "react";

// Create a shared context for user details and login/logout functions
const AuthContext = createContext(null);

export default AuthContext;