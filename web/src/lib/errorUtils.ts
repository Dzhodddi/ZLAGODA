import { isAxiosError } from "axios";

const STATUS_MESSAGES: Record<number, string> = {
    400: "Цей елемент використовується або вже існує",
    401: "Необхідна авторизація",
    403: "Доступ заборонений",
    404: "Не знайдено",
    409: "Цей елемент використовується",
    422: "Некоректні дані",
    500: "Помилка сервера",
};

export const getErrorMessage = (error: unknown, fallback: string): string => {
    if (isAxiosError(error) && error.response) {
        const { status } = error.response;
        console.error(error);
        return STATUS_MESSAGES[status] ?? fallback;
    }
    if (isAxiosError(error) && !error.response) {
        return "Помилка підключення до сервера";
    }
    return fallback;
};
