import { Navigate, Outlet } from "react-router-dom";
import useAuth from "../context/useAuth";

function ProtectedRoute({ allowedRoles }) {
    const { user, isAuthenticated } = useAuth();

    // Send users to login if no user is currently signed in
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    // Redirect users who do not have an allowed role
    if (
        allowedRoles &&
        !allowedRoles.includes(user.role)
    ) {
        const correctPath =
            user.role === "ORGANISER"
                ? "/organiser/events"
                : "/events";

        return <Navigate to={correctPath} replace />;
    }

    // Show the matching child route after the checks pass
    return <Outlet />;
}

export default ProtectedRoute;