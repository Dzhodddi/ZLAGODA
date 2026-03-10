import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";

interface Props {
    allowedRoles?: ("MANAGER" | "CASHIER")[];
}

export const ProtectedRoute = ({ allowedRoles }: Props) => {
    const { accessToken, role } = useAuthStore();

    if (!accessToken) return <Navigate to="/login" replace />;

    if (allowedRoles && !allowedRoles.includes(role as "MANAGER" | "CASHIER")) {
        return <Navigate to="/unauthorized" replace />;
    }

    return <Outlet />;
};
