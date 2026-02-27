import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type LoginResponse } from "@/features/auth/types/types.ts";
import { useNavigate } from "react-router-dom";
import {login} from "@/features/auth/api/authApi.ts";
import { jwtDecode } from "jwt-decode";
import {useAuthStore} from "@/store/authStore.ts";

export const useLogin = () => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const setTokens = useAuthStore((state) => state.setTokens);

    return useMutation({
        mutationFn: login,
        onSuccess: (payload: LoginResponse) => {
            setTokens(payload.accessToken, payload.refreshToken);
            const decoded = jwtDecode(payload.accessToken)
            queryClient.setQueryData(["authUser"], decoded);
            navigate("/card");
        },
        onError: (error) => {
            console.error("Login failed:", error);
        },
    });
};