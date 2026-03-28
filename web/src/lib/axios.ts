import applyCaseMiddleware from 'axios-case-converter';
import { useAuthStore } from "@/store/authStore.ts";
import axios, { type AxiosInstance } from 'axios';

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

const addAuthToken = (config: any) => {
    const { accessToken } = useAuthStore.getState();
    if (accessToken && config.headers) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
};

goApiClient.interceptors.request.use(addAuthToken, (error) => Promise.reject(error));
javaApiClient.interceptors.request.use(addAuthToken, (error) => Promise.reject(error));

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

const createAuthInterceptor = (client: AxiosInstance) => async (error: any) => {
    const originalRequest = error.config;

    if (originalRequest.url?.includes('/login') || originalRequest.url?.includes('/refresh')) {
        return Promise.reject(error);
    }

    if (error.response?.status === 401 && !originalRequest._retry) {

        if (isRefreshing) {
            try {
                const token = await new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                });
                originalRequest.headers.Authorization = `Bearer ${token}`;
                return client(originalRequest);
            } catch (err) {
                return Promise.reject(err);
            }
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const { refreshToken, setTokens, role } = useAuthStore.getState();
            if (!refreshToken) throw new Error("No refresh token");

            const { data } = await axios.post(`http://localhost:8081/api/v1/auth/refresh`, { refreshToken });

            if (!role) throw new Error("No role");

            setTokens(data.accessToken, data.refreshToken, role);
            processQueue(null, data.accessToken);
            originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
            return client(originalRequest);

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
    createAuthInterceptor(goApiClient)
);

javaApiClient.interceptors.response.use(
    (response) => response,
    createAuthInterceptor(javaApiClient)
);
