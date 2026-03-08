import axios from 'axios';
import applyCaseMiddleware from 'axios-case-converter';
import { useAuthStore } from "@/store/authStore.ts";

export const goApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8080/api/v1",
    timeout: 5000,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
}));

export const javaApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8081/api/v1",
    timeout: 5000,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
}));

// --- Auth request interceptor (для обох клієнтів) ---
const addAuthToken = (config: any) => {
    const { accessToken } = useAuthStore.getState();
    if (accessToken && config.headers) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
};

goApiClient.interceptors.request.use(addAuthToken, (error) => Promise.reject(error));
javaApiClient.interceptors.request.use(addAuthToken, (error) => Promise.reject(error));

// --- Refresh token логіка ---
let isRefreshing = false;
let failedQueue: Array<{
    resolve: (value?: unknown) => void;
    reject: (error?: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach((promise) => {
        if (error) promise.reject(error);
        else promise.resolve(token);
    });
    failedQueue = [];
};

const authInterceptor = async (error: any) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {

        if (isRefreshing) {
            try {
                const token = await new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                });
                originalRequest.headers.Authorization = `Bearer ${token}`;
                return goApiClient(originalRequest);
            } catch (err) {
                return Promise.reject(err);
            }
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const { refreshToken, setTokens, role } = useAuthStore.getState();
            if (!refreshToken) {
                throw new Error("No refresh token");
            }

            const response = await axios.post("http://localhost:8081/api/v1/auth/refresh", {
                refreshToken,
            });

            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data;
            if (!role) {
                throw new Error("No role");
            }
            setTokens(newAccessToken, newRefreshToken, role);

            processQueue(null, newAccessToken);
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return goApiClient(originalRequest);

        } catch (refreshError) {
            processQueue(refreshError, null);
            useAuthStore.getState().clearTokens();
            window.location.href = "/login";
            return Promise.reject(refreshError);

        } finally {
            isRefreshing = false;
        }
    }

    return Promise.reject(error);
};

goApiClient.interceptors.response.use(
    (response) => response,
    (error) => authInterceptor(error)  // ← return прибрано, бо async функція вже повертає Promise
);

javaApiClient.interceptors.response.use(
    (response) => response,
    (error) => authInterceptor(error)
);
