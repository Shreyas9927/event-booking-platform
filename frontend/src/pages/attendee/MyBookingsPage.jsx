import {
    useEffect,
    useState,
} from "react";
import {
    Ban,
    CalendarClock,
    ReceiptText,
    TicketCheck,
} from "lucide-react";
import {
    cancelBooking,
    getMyBookings,
} from "../../services/bookingService";

function MyBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [cancellingId, setCancellingId] =
        useState(null);
    const [successMessage, setSuccessMessage] =
        useState("");
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        let isActive = true;

        getMyBookings()
            .then((bookingData) => {
                if (isActive) {
                    setBookings(bookingData);
                }
            })
            .catch((error) => {
                if (isActive) {
                    setErrorMessage(
                        error.response?.data?.errorMessage ||
                        "Unable to load your bookings.",
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

    const handleCancellation = async (bookingId) => {
        setSuccessMessage("");
        setErrorMessage("");
        setCancellingId(bookingId);

        try {
            const cancelledBooking =
                await cancelBooking(bookingId);

            setBookings((currentBookings) =>
                currentBookings.map((booking) =>
                    booking.id === bookingId
                        ? cancelledBooking
                        : booking,
                ),
            );

            setSuccessMessage(
                "Your booking was cancelled successfully.",
            );
        } catch (error) {
            setErrorMessage(
                error.response?.data?.errorMessage ||
                "Unable to cancel this booking.",
            );
        } finally {
            setCancellingId(null);
        }
    };

    const formatDate = (date) =>
        new Intl.DateTimeFormat("en-IN", {
            dateStyle: "medium",
            timeStyle: "short",
        }).format(new Date(date));

    return (
        <main>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">
                        YOUR RESERVATIONS
                    </p>

                    <h1>My bookings</h1>

                    <p>
                        View and manage all your ticket bookings.
                    </p>
                </div>

                <ReceiptText size={42} />
            </section>

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
                    Loading your bookings...
                </div>
            )}

            {!isLoading && bookings.length === 0 && (
                <div className="empty-state">
                    <TicketCheck size={42} />
                    <h2>No bookings yet</h2>
                    <p>
                        Book an upcoming event and it will appear
                        here.
                    </p>
                </div>
            )}

            {!isLoading && bookings.length > 0 && (
                <section className="booking-list">
                    {bookings.map((booking) => (
                        <article
                            className="booking-card"
                            key={booking.id}
                        >
                            <div className="booking-icon">
                                <TicketCheck size={24} />
                            </div>

                            <div className="booking-details">
                                <div className="booking-title-row">
                                    <div>
                                        <h2>{booking.eventName}</h2>

                                        <span className="booking-reference">
                      {booking.bookingReference}
                    </span>
                                    </div>

                                    <span
                                        className={`status-badge ${booking.status.toLowerCase()}`}
                                    >
                    {booking.status}
                  </span>
                                </div>

                                <div className="booking-meta">
                  <span>
                    <CalendarClock size={17} />
                      {formatDate(booking.eventDate)}
                  </span>

                                    <span>
                    <TicketCheck size={17} />
                                        {booking.quantity} ticket(s)
                  </span>

                                    <span>
                    Booked {formatDate(booking.bookedAt)}
                  </span>
                                </div>
                            </div>

                            {booking.status === "CONFIRMED" && (
                                <button
                                    className="cancel-button"
                                    type="button"
                                    disabled={
                                        cancellingId === booking.id
                                    }
                                    onClick={() =>
                                        handleCancellation(booking.id)
                                    }
                                >
                                    <Ban size={17} />

                                    {cancellingId === booking.id
                                        ? "Cancelling..."
                                        : "Cancel booking"}
                                </button>
                            )}
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}

export default MyBookingsPage;