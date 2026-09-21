import { useState } from "react";
import {
    CalendarClock,
    Ticket,
    UserRound,
} from "lucide-react";

function EventCard({
                       event,
                       onBook,
                       isBooking,
                   }) {
    // Each event card starts with one ticket selected
    const [quantity, setQuantity] = useState(1);

    // Decide whether booking should be disabled for this event
    const isSoldOut = event.availableTickets === 0;

    // Convert backend date into a readable Indian date and time format
    const formattedDate = new Intl.DateTimeFormat(
        "en-IN",
        {
            dateStyle: "medium",
            timeStyle: "short",
        },
    ).format(new Date(event.eventDate));

    const handleBooking = () => {
        // Send event ID and selected quantity to EventsPage
        onBook(event.id, Number(quantity));
    };

    return (
        <article className="event-card">
            <div className="event-card-accent" />

            <div className="event-card-content">
                <div className="event-card-top">
                    <span
                        className={
                            isSoldOut
                                ? "availability-badge sold-out"
                                : "availability-badge"
                        }
                    >
                        {isSoldOut
                            ? "Sold out"
                            : `${event.availableTickets} tickets left`}
                    </span>

                    <span className="event-capacity">
                        {event.bookedTickets}/{event.capacity} booked
                    </span>
                </div>

                <h2>{event.name}</h2>

                <p className="event-description">
                    {event.description}
                </p>

                <div className="event-meta">
                    <div>
                        <CalendarClock size={18} />
                        <span>{formattedDate}</span>
                    </div>

                    <div>
                        <UserRound size={18} />
                        <span>
                            Organised by {event.organiserName}
                        </span>
                    </div>
                </div>

                <div className="event-booking">
                    <div className="quantity-control">
                        <Ticket size={18} />

                        <label htmlFor={`quantity-${event.id}`}>
                            Tickets
                        </label>

                        <input
                            id={`quantity-${event.id}`}
                            type="number"
                            min="1"
                            max={Math.max(event.availableTickets, 1)}
                            value={quantity}
                            disabled={isSoldOut || isBooking}
                            onChange={(eventObject) =>
                                setQuantity(eventObject.target.value)
                            }
                        />
                    </div>

                    <button
                        className="primary-button card-button"
                        type="button"
                        disabled={isSoldOut || isBooking}
                        onClick={handleBooking}
                    >
                        {isBooking
                            ? "Booking..."
                            : isSoldOut
                                ? "Sold out"
                                : "Book tickets"}
                    </button>
                </div>
            </div>
        </article>
    );
}

export default EventCard;