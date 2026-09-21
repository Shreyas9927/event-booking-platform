import { Outlet } from "react-router-dom";
import AppHeader from "./AppHeader";

function AppLayout() {
    return (
        <div className="application-shell">
            {/* Show the shared header across these pages */}
            <AppHeader />

            <div className="page-container">
                {/* Display the page matching the current URL */}
                <Outlet />
            </div>
        </div>
    );
}

export default AppLayout;