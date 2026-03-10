import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type LoginResponse } from "@/features/auth/types/types.ts";
import { useNavigate } from "react-router-dom";
import {login, register} from "@/features/auth/api/authApi.ts";
import { jwtDecode } from "jwt-decode";
import { useAuthStore } from "@/store/authStore.ts";

interface JwtPayload {
    sub: string;
    roles: ("MANAGER" | "CASHIER")[];
}

export const useRegister = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: register,
        onSuccess: () => {
            alert("Registered! Please login.");
            navigate("/login");
        },
        onError: (error: any) => {
            console.error("Registration failed:", error.response?.data);
        },
    });
};
import {useAuthStore} from "@/store/authStore.ts";
import {toast} from "sonner";

export const useLogin = () => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const setTokens = useAuthStore((state) => state.setTokens);

    return useMutation({
        mutationFn: login,
        onSuccess: (payload: LoginResponse) => {
            const decoded = jwtDecode<JwtPayload>(payload.accessToken);
            const role = decoded.roles[0];
            setTokens(payload.accessToken, payload.refreshToken, role!);
            queryClient.setQueryData(["authUser"], decoded);

            if (role === "MANAGER") {
                navigate("/employee");
            } else {
                navigate("/employee/me");
            }
            toast.success("Successfully login")
            navigate("/");
        },
        onError: (error) => {
            toast.error("Failed to login")
            console.error(error)
        },
    });
};
