import { useState } from "react";
import {
    Link,
    useLocation,
    useNavigate,
} from "react-router-dom";
import {
    CalendarDays,
    LockKeyhole,
    Mail,
} from "lucide-react";
import useAuth from "../../context/useAuth";

function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();

    // Read a message passed when navigating to the login page
    const successMessage = location.state?.message;

    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });

    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Update the changed field while keeping the other field unchanged
    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((currentData) => ({
            ...currentData,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        // Submit through React without reloading the browser page
        event.preventDefault();
        setErrorMessage("");
        setIsSubmitting(true);

        try {
            const loggedInUser = await login(formData);

            // Open the home page for the logged-in user's role
            if (loggedInUser.role === "ORGANISER") {
                navigate("/organiser/events");
            } else {
                navigate("/events");
            }
        } catch (error) {
            // Show the backend error, or a fallback if none is available
            setErrorMessage(
                error.response?.data?.errorMessage ||
                "Unable to login. Please try again.",
            );
        } finally {
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
                    <p className="eyebrow">
                        PLAN. DISCOVER. EXPERIENCE.
                    </p>

                    <h1>
                        Every great experience starts with an event.
                    </h1>

                    <p>
                        Create memorable events or discover and book your next
                        experience—all from one secure platform.
                    </p>
                </div>
            </section>

            <section className="auth-form-panel">
                <div className="auth-card">
                    <p className="eyebrow">WELCOME BACK</p>

                    <h2>Sign in to your account</h2>

                    <p className="auth-subtitle">
                        Enter your credentials to continue.
                    </p>

                    {/* Display messages only when they contain text */}
                    {successMessage && (
                        <div className="form-success" role="status">
                            {successMessage}
                        </div>
                    )}

                    {errorMessage && (
                        <div className="form-error" role="alert">
                            {errorMessage}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <label htmlFor="email">
                            Email address
                        </label>

                        <div className="input-wrapper">
                            <Mail size={18} />

                            <input
                                id="email"
                                name="email"
                                type="email"
                                placeholder="name@example.com"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <label htmlFor="password">
                            Password
                        </label>

                        <div className="input-wrapper">
                            <LockKeyhole size={18} />

                            <input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Enter your password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        {/* Disable the button while the login request is pending */}
                        <button
                            className="primary-button"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            {isSubmitting
                                ? "Signing in..."
                                : "Sign in"}
                        </button>
                    </form>

                    <p className="auth-switch">
                        Don&apos;t have an account?{" "}
                        <Link to="/register">
                            Create account
                        </Link>
                    </p>
                </div>
            </section>
        </main>
    );
}

export default LoginPage;