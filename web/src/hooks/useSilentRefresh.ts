import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";
import {javaApiClient} from "@/lib/axios.ts";
import { jwtDecode } from "jwt-decode";

export const useSilentRefresh = () => {
    const [isReady, setIsReady] = useState(false);
    const { refreshToken, accessToken, setTokens, clearTokens } = useAuthStore();

    useEffect(() => {
        if (refreshToken && !accessToken) {
            javaApiClient
                .post("/auth/refresh", { refreshToken })
                .then(({ data }) => {
                    const decoded = jwtDecode<{ roles: ("MANAGER" | "CASHIER")[] }>(data.accessToken);
                    setTokens(data.accessToken, data.refreshToken, decoded.roles[0]!);
                })
                .catch(() => clearTokens())
                .finally(() => setIsReady(true));
        } else {
            setIsReady(true);
        }
    }, []);

    return { isReady };
};
