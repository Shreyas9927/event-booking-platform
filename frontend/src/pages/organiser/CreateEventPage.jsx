import { useState } from "react";
import {
    Link,
    useNavigate,
} from "react-router-dom";
import {
    CalendarPlus,
    FileText,
    Ticket,
    Type,
} from "lucide-react";
import {
    createEvent,
} from "../../services/eventService";

const currentDate = new Date();

const minimumDateTime = new Date(
    currentDate.getTime() -
    currentDate.getTimezoneOffset() * 60000,
)
    .toISOString()
    .slice(0, 16);

function CreateEventPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: "",
        description: "",
        eventDate: "",
        capacity: "",
    });

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((currentData) => ({
            ...currentData,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage("");
        setIsSubmitting(true);

        try {
            await createEvent({
                ...formData,
                capacity: Number(formData.capacity),
            });

            navigate("/organiser/events", {
                state: {
                    message: "Event created successfully.",
                },
            });
        } catch (error) {
            setErrorMessage(
                error.response?.data?.errorMessage ||
                "Unable to create the event.",
            );
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <main>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">
                        NEW EXPERIENCE
                    </p>

                    <h1>Create an event</h1>

                    <p>
                        Add the event information and ticket
                        capacity.
                    </p>
                </div>

                <CalendarPlus size={42} />
            </section>

            <section className="event-form-card">
                {errorMessage && (
                    <div
                        className="form-error page-message"
                        role="alert"
                    >
                        {errorMessage}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-field">
                        <label htmlFor="name">
                            Event name
                        </label>

                        <div className="input-wrapper">
                            <Type size={18} />

                            <input
                                id="name"
                                name="name"
                                type="text"
                                placeholder="Example: Java Developer Conference"
                                value={formData.name}
                                onChange={handleChange}
                                minLength={3}
                                maxLength={150}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-field">
                        <label htmlFor="description">
                            Description
                        </label>

                        <div className="textarea-wrapper">
                            <FileText size={18} />

                            <textarea
                                id="description"
                                name="description"
                                placeholder="Describe the event..."
                                value={formData.description}
                                onChange={handleChange}
                                maxLength={2000}
                                rows={6}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label htmlFor="eventDate">
                                Event date and time
                            </label>

                            <div className="input-wrapper">
                                <CalendarPlus size={18} />

                                <input
                                    id="eventDate"
                                    name="eventDate"
                                    type="datetime-local"
                                    min={minimumDateTime}
                                    value={formData.eventDate}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-field">
                            <label htmlFor="capacity">
                                Ticket capacity
                            </label>

                            <div className="input-wrapper">
                                <Ticket size={18} />

                                <input
                                    id="capacity"
                                    name="capacity"
                                    type="number"
                                    min="1"
                                    placeholder="Example: 100"
                                    value={formData.capacity}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <div className="form-actions">
                        <Link
                            className="secondary-button"
                            to="/organiser/events"
                        >
                            Cancel
                        </Link>

                        <button
                            className="primary-button submit-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            <CalendarPlus size={18} />

                            {isSubmitting
                                ? "Creating event..."
                                : "Create event"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    );
}

export default CreateEventPage;