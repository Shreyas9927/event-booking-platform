import {
    useEffect,
    useState,
} from "react";
import {
    Link,
    useLocation,
} from "react-router-dom";
import {
    CalendarClock,
    PlusCircle,
    Ticket,
    UsersRound,
} from "lucide-react";
import {
    getMyEvents,
} from "../../services/eventService";

function OrganiserEventsPage() {
    // Reads the success message sent from CreateEventPage after creation
    const location = useLocation();
    const successMessage = location.state?.message;

    const [events, setEvents] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        // Prevent state updates if the user leaves this page before API finishes
        let isActive = true;

        // Backend identifies the organiser from the JWT token
        getMyEvents()
            .then((eventData) => {
                if (isActive) {
                    setEvents(eventData);
                }
            })
            .catch((error) => {
                if (isActive) {
                    setErrorMessage(
                        error.response?.data?.errorMessage ||
                        "Unable to load your events.",
                    );
                }
            })
            .finally(() => {
                if (isActive) {
                    setIsLoading(false);
                }
            });

        return () => {
            isActive = false;
        };
    }, []);

    // Convert backend date into a readable Indian date and time format
    const formatDate = (date) =>
        new Intl.DateTimeFormat("en-IN", {
            dateStyle: "medium",
            timeStyle: "short",
        }).format(new Date(date));

    return (
        <main>
            <section className="organiser-heading">
                <div>
                    <p className="eyebrow">
                        ORGANISER DASHBOARD
                    </p>

                    <h1>My events</h1>

                    <p>
                        Track event capacity, availability and
                        ticket bookings.
                    </p>
                </div>

                <Link
                    className="primary-button heading-button"
                    to="/organiser/events/create"
                >
                    <PlusCircle size={18} />
                    Create event
                </Link>
            </section>

            {/* Show message after an event is created successfully */}
            {successMessage && (
                <div className="form-success page-message">
                    {successMessage}
                </div>
            )}

            {errorMessage && (
                <div
                    className="form-error page-message"
                    role="alert"
                >
                    {errorMessage}
                </div>
            )}

            {isLoading && (
                <div className="loading-state">
                    Loading your events...
                </div>
            )}

            {!isLoading && events.length === 0 && (
                <div className="empty-state">
                    <CalendarClock size={42} />

                    <h2>No events created yet</h2>

                    <p>
                        Create your first event to start accepting
                        ticket bookings.
                    </p>
                </div>
            )}

            {!isLoading && events.length > 0 && (
                <section className="organiser-event-grid">
                    {events.map((event) => (
                        <article
                            className="organiser-event-card"
                            key={event.id}
                        >
                            <div className="organiser-event-header">
                                <div>
                                    <span className="event-id">
                                        EVENT #{event.id}
                                    </span>

                                    <h2>{event.name}</h2>
                                </div>

                                <CalendarClock size={24} />
                            </div>

                            <p className="event-description">
                                {event.description}
                            </p>

                            <p className="organiser-event-date">
                                <CalendarClock size={17} />
                                {formatDate(event.eventDate)}
                            </p>

                            <div className="event-statistics">
                                <div>
                                    <UsersRound size={20} />
                                    <span>Total capacity</span>
                                    <strong>{event.capacity}</strong>
                                </div>

                                <div>
                                    <Ticket size={20} />
                                    <span>Tickets booked</span>
                                    <strong>
                                        {event.bookedTickets}
                                    </strong>
                                </div>

                                <div>
                                    <Ticket size={20} />
                                    <span>Available</span>
                                    <strong>
                                        {event.availableTickets}
                                    </strong>
                                </div>
                            </div>

                            <div className="booking-progress">
                                {/* Width changes based on the percentage of tickets booked */}
                                <div
                                    style={{
                                        width: `${
                                            (event.bookedTickets /
                                                event.capacity) *
                                            100
                                        }%`,
                                    }}
                                />
                            </div>
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}

export default OrganiserEventsPage;