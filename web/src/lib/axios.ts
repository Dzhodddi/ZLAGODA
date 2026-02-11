import axios from 'axios';
import applyCaseMiddleware from 'axios-case-converter';

export const goApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8080/api/v1",
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    }
}))

export const javaApiClient = applyCaseMiddleware(axios.create({
    baseURL: "http://localhost:8081/api/v1",
    timeout: 1000,
    headers: {
        "Content-Type": "application/json",
    }
}))

goApiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);