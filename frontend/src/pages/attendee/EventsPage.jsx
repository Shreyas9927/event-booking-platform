import {
    useEffect,
    useState,
} from "react";
import {
    CalendarSearch,
    Sparkles,
} from "lucide-react";
import EventCard from "../../components/EventCard";
import {
    getUpcomingEvents,
} from "../../services/eventService";
import {
    bookTickets,
} from "../../services/bookingService";

function EventsPage() {
    const [events, setEvents] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [bookingEventId, setBookingEventId] =
        useState(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] =
        useState("");

    useEffect(() => {
        // Prevent state updates if the user leaves this page before API response
        let isActive = true;

        // Load upcoming events when the page is first displayed
        getUpcomingEvents()
            .then((eventData) => {
                if (isActive) {
                    setEvents(eventData);
                }
            })
            .catch((error) => {
                if (isActive) {
                    setErrorMessage(
                        error.response?.data?.errorMessage ||
                        "Unable to load upcoming events.",
                    );
                }
            })
            .finally(() => {
                if (isActive) {
                    setIsLoading(false);
                }
            });

        // Mark component as inactive when user leaves this page
        return () => {
            isActive = false;
        };
    }, []);

    const handleBooking = async (
        eventId,
        quantity,
    ) => {
        setErrorMessage("");
        setSuccessMessage("");

        // Track only the event currently being booked
        setBookingEventId(eventId);

        try {
            const booking = await bookTickets(
                eventId,
                quantity,
            );

            setSuccessMessage(
                `${quantity} ticket(s) booked successfully. Reference: ${booking.bookingReference}`,
            );

            // Reload events to show the latest available ticket count
            const updatedEvents =
                await getUpcomingEvents();

            setEvents(updatedEvents);
        } catch (error) {
            setErrorMessage(
                error.response?.data?.errorMessage ||
                "Unable to complete the booking.",
            );
        } finally {
            setBookingEventId(null);
        }
    };

    return (
        <main>
            <section className="page-hero">
                <div>
                    <p className="eyebrow">
                        <Sparkles size={14} />
                        UPCOMING EXPERIENCES
                    </p>

                    <h1>Discover your next event</h1>

                    <p>
                        Browse upcoming events and reserve your
                        tickets securely.
                    </p>
                </div>

                <CalendarSearch size={46} />
            </section>

            {/* Show booking success message only when booking succeeds */}
            {successMessage && (
                <div className="form-success page-message">
                    {successMessage}
                </div>
            )}

            {/* Show API error only when an error message exists */}
            {errorMessage && (
                <div
                    className="form-error page-message"
                    role="alert"
                >
                    {errorMessage}
                </div>
            )}

            {/* Show loading state while upcoming events are being fetched */}
            {isLoading && (
                <div className="loading-state">
                    Loading upcoming events...
                </div>
            )}

            {/* Show empty state when API returns no upcoming events */}
            {!isLoading && events.length === 0 && (
                <div className="empty-state">
                    <CalendarSearch size={42} />

                    <h2>No upcoming events</h2>

                    <p>
                        There are no upcoming events available
                        right now.
                    </p>
                </div>
            )}

            {/* Create one EventCard component for each available event */}
            {!isLoading && events.length > 0 && (
                <section className="event-grid">
                    {events.map((event) => (
                        <EventCard
                            key={event.id}
                            event={event}
                            onBook={handleBooking}
                            isBooking={bookingEventId === event.id}
                        />
                    ))}
                </section>
            )}
        </main>
    );
}

export default EventsPage;