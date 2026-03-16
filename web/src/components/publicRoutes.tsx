import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";

export const PublicRoute = () => {
    const { accessToken } = useAuthStore();

    if (accessToken) {
        return <Navigate to="/" />;
    }

    return <Outlet />;
};