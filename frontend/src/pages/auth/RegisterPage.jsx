import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
    CalendarDays,
    LockKeyhole,
    Mail,
    UserRound,
    UsersRound,
} from "lucide-react";
import { registerUser } from "../../services/authService";

function RegisterPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        role: "ATTENDEE",
    });

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Update the field that the user changes
    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((currentData) => ({
            ...currentData,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        // Prevent normal form submission and page reload
        event.preventDefault();
        setErrorMessage("");
        setIsSubmitting(true);

        try {
            // Send registration details to the backend
            await registerUser(formData);

            // Redirect to login page with registration success message
            navigate("/login", {
                state: {
                    message: "Registration successful. You can now sign in.",
                },
            });
        } catch (error) {
            // Display backend error or fallback message
            setErrorMessage(
                error.response?.data?.errorMessage ||
                "Unable to create your account. Please try again.",
            );
        } finally {
            // Enable the submit button after request completion
            setIsSubmitting(false);
        }
    };

    return (
        <main className="auth-page">
            <section className="auth-brand-panel">
                <div className="brand-mark">
                    <CalendarDays size={28} />
                    <span>EventFlow</span>
                </div>

                <div className="brand-message">
                    <p className="eyebrow">JOIN THE COMMUNITY</p>

                    <h1>
                        Turn plans into experiences worth remembering.
                    </h1>

                    <p>
                        Join as an organiser to create events or as an attendee
                        to discover and book them.
                    </p>
                </div>
            </section>

            <section className="auth-form-panel">
                <div className="auth-card">
                    <p className="eyebrow">GET STARTED</p>

                    <h2>Create your account</h2>

                    <p className="auth-subtitle">
                        Choose your role and enter your details.
                    </p>

                    {/* Show an error only when registration fails */}
                    {errorMessage && (
                        <div className="form-error" role="alert">
                            {errorMessage}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <label htmlFor="name">Full name</label>

                        <div className="input-wrapper">
                            <UserRound size={18} />

                            <input
                                id="name"
                                name="name"
                                type="text"
                                placeholder="Enter your full name"
                                value={formData.name}
                                onChange={handleChange}
                                minLength={2}
                                maxLength={100}
                                required
                            />
                        </div>

                        <label htmlFor="email">Email address</label>

                        <div className="input-wrapper">
                            <Mail size={18} />

                            <input
                                id="email"
                                name="email"
                                type="email"
                                placeholder="name@example.com"
                                value={formData.email}
                                onChange={handleChange}
                                maxLength={150}
                                required
                            />
                        </div>

                        <label htmlFor="password">Password</label>

                        <div className="input-wrapper">
                            <LockKeyhole size={18} />

                            <input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Minimum 8 characters"
                                value={formData.password}
                                onChange={handleChange}
                                minLength={8}
                                maxLength={72}
                                required
                            />
                        </div>

                        <label htmlFor="role">I want to join as</label>

                        <div className="input-wrapper">
                            <UsersRound size={18} />

                            <select
                                id="role"
                                name="role"
                                value={formData.role}
                                onChange={handleChange}
                            >
                                <option value="ATTENDEE">Attendee</option>
                                <option value="ORGANISER">Organiser</option>
                            </select>
                        </div>

                        {/* Disable button while the registration request is running */}
                        <button
                            className="primary-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            {isSubmitting
                                ? "Creating account..."
                                : "Create account"}
                        </button>
                    </form>

                    <p className="auth-switch">
                        Already have an account?{" "}
                        <Link to="/login">Sign in</Link>
                    </p>
                </div>
            </section>
        </main>
    );
}

export default RegisterPage;