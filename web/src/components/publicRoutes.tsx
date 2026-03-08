import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";

export const PublicRoute = () => {
    const { accessToken, role } = useAuthStore();

    if (accessToken) {
        return <Navigate to={role === "MANAGER" ? "/employee" : "/employee/me"} replace />;
    }

    return <Outlet />;
};