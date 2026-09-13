import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";
import AppLayout from "./components/AppLayout";
import useAuth from "./context/useAuth";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import EventsPage from "./pages/attendee/EventsPage";
import MyBookingsPage from "./pages/attendee/MyBookingsPage";
import CreateEventPage from "./pages/organiser/CreateEventPage";
import OrganiserEventsPage from "./pages/organiser/OrganiserEventsPage";
import ProtectedRoute from "./routes/ProtectedRoute";
import "./styles/global.css";

function App() {
  const { user, isAuthenticated } = useAuth();

  const homePath =
      user?.role === "ORGANISER"
          ? "/organiser/events"
          : "/events";

  return (
      <Routes>
        <Route
            path="/"
            element={
              <Navigate
                  to={isAuthenticated ? homePath : "/login"}
                  replace
              />
            }
        />

        <Route path="/login" element={<LoginPage />} />

        <Route
            path="/register"
            element={<RegisterPage />}
        />

        <Route
            element={
              <ProtectedRoute
                  allowedRoles={["ATTENDEE"]}
              />
            }
        >
          <Route element={<AppLayout />}>
            <Route
                path="/events"
                element={<EventsPage />}
            />

            <Route
                path="/bookings"
                element={<MyBookingsPage />}
            />
          </Route>
        </Route>

        <Route
            element={
              <ProtectedRoute
                  allowedRoles={["ORGANISER"]}
              />
            }
        >
          <Route element={<AppLayout />}>
            <Route
                path="/organiser/events"
                element={<OrganiserEventsPage />}
            />

            <Route
                path="/organiser/events/create"
                element={<CreateEventPage />}
            />
          </Route>
        </Route>

        <Route
            path="*"
            element={
              <Navigate
                  to={isAuthenticated ? homePath : "/login"}
                  replace
              />
            }
        />
      </Routes>
  );
}

export default App;