import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export const useSilentRefresh = () => {
    const [isReady, setIsReady] = useState(false);
    const { refreshToken, accessToken, setTokens, clearTokens } = useAuthStore();

    useEffect(() => {
        if (refreshToken && !accessToken) {
            axios.post(`http://localhost:8081/api/v1/auth/refresh`, { refreshToken })
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
