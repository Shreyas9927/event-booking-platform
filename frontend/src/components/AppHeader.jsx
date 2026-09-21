import {
    CalendarDays,
    LogOut,
    PlusCircle,
    Ticket,
} from "lucide-react";
import {
    NavLink,
    useNavigate,
} from "react-router-dom";
import useAuth from "../context/useAuth";

function AppHeader() {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    // Clear login information and open the login page
    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <header className="app-header">
            <div className="header-container">
                <NavLink className="header-brand" to="/">
                    <CalendarDays size={24} />
                    <span>EventFlow</span>
                </NavLink>

                <nav className="header-navigation">
                    {/* Show attendee navigation links */}
                    {user.role === "ATTENDEE" && (
                        <>
                            <NavLink to="/events">
                                <CalendarDays size={17} />
                                Events
                            </NavLink>

                            <NavLink to="/bookings">
                                <Ticket size={17} />
                                My bookings
                            </NavLink>
                        </>
                    )}

                    {/* Show organiser navigation links */}
                    {user.role === "ORGANISER" && (
                        <>
                            <NavLink to="/organiser/events">
                                <CalendarDays size={17} />
                                My events
                            </NavLink>

                            <NavLink to="/organiser/events/create">
                                <PlusCircle size={17} />
                                Create event
                            </NavLink>
                        </>
                    )}
                </nav>

                <div className="header-user">
                    <div className="user-summary">
                        <strong>{user.name}</strong>
                        <span>
                            {user.role === "ORGANISER"
                                ? "Organiser"
                                : "Attendee"}
                        </span>
                    </div>

                    <button
                        className="logout-button"
                        type="button"
                        onClick={handleLogout}
                        title="Sign out"
                    >
                        <LogOut size={18} />
                    </button>
                </div>
            </div>
        </header>
    );
}

export default AppHeader;