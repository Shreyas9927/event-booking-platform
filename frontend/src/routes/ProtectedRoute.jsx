import { Navigate, Outlet } from "react-router-dom";
import useAuth from "../context/useAuth";

function ProtectedRoute({ allowedRoles }) {
    const { user, isAuthenticated } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

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

    return <Outlet />;
}

export default ProtectedRoute;