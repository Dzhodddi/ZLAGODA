import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type LoginResponse } from "@/features/auth/types/types.ts";
import { useNavigate } from "react-router-dom";
import {login, register} from "@/features/auth/api/authApi.ts";
import { jwtDecode } from "jwt-decode";
import {useAuthStore} from "@/store/authStore.ts";
import {toast} from "sonner";
import {isAxiosError} from "axios";

interface JwtPayload {
    sub: string;
    roles: ("MANAGER" | "CASHIER")[];
}

export const useRegister = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: register,
        onSuccess: () => {
            toast.success("Успішно зареєстровано");
            navigate("/login");
        },
        onError: (error) => {
            if (isAxiosError(error) && error.response?.data) {
                const backendError = error.response.data;

                if (error.response.status === 409 || backendError.error === "Conflict") {
                    toast.error("Працівник з таким ID вже існує!");
                    return;
                }
                toast.error("Помилка реєстрації");
                return;
            }
            toast.error("Помилка підключення до сервера");
            console.error(error);
        },
    });
};

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
            toast.success("Успішно авторизовано")
            navigate("/", { replace: true });
        },
        onError: (error) => {
            toast.error("Помилка авторизації")
            console.error(error)
        },
    });
};
