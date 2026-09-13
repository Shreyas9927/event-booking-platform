import { Outlet } from "react-router-dom";
import AppHeader from "./AppHeader";

function AppLayout() {
    return (
        <div className="application-shell">
            <AppHeader />

            <div className="page-container">
                <Outlet />
            </div>
        </div>
    );
}

export default AppLayout;