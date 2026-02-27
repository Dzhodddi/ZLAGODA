import axios from 'axios';
import applyCaseMiddleware from 'axios-case-converter';
import {useAuthStore} from "@/store/authStore.ts";

export const goApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8080/api/v1",
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
}))

export const javaApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8081/api/v1",
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
}))

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

goApiClient.interceptors.request.use(
    (config) => {
        const { accessToken } = useAuthStore.getState();

        if (accessToken && config.headers) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

const authInterceptor = async (error: any) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {

        if (isRefreshing) {
            try {
                const token = await new Promise((resolve, reject) => {
                    failedQueue.push({resolve, reject});
                });
                originalRequest.headers.Authorization = `Bearer ${token}`;
                return await goApiClient(originalRequest);
            } catch (err) {
                return await Promise.reject(err);
            }
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const { refreshToken, setTokens, clearTokens } = useAuthStore.getState();

            if (!refreshToken) throw new Error("No refresh token available");

            const response = await axios.post("http://localhost:8081/api/v1/auth/refresh", {
                refreshToken: refreshToken,
            });

            const newAccessToken = response.data.accessToken;
            const newRefreshToken = response.data.refreshToken;

            setTokens(newAccessToken, newRefreshToken);

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
}

goApiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        await authInterceptor(error)
    }
);

javaApiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        await authInterceptor(error)
    }
);