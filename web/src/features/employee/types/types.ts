import {z} from 'zod';

const phoneRegex = new RegExp("^(\\+380|0)\\d{9}$");

export const BaseEmployeeSchema = z.object({
    idEmployee: z
        .string()
        .min(1, "ID занадто короткий")
        .max(10, "ID занадто довгий"),
    emplSurname: z
        .string()
        .min(1, "Прізвище занадто коротке")
        .max(50, "Прізвище занадто довге"),
    emplName: z
        .string()
        .min(1, "Ім'я занадто коротке")
        .max(50, "Ім'я занадто довге"),
    emplPatronymic: z
        .string()
        .max(50, "По батькові занадто довге")
        .nullable()
        .optional()
        .transform((val) => val?.trim() === "" || val === undefined ? null : val),
    role: z
        .enum(
            ["MANAGER", "CASHIER"]
        ),
    salary: z
        .coerce
        .number("Введіть число")
        .min(0, "Зарплата має бути позитивною")
        .max(999999999.9999, "Зарплата завелика"),
    dateOfBirth: z
        .iso
        .date("Неправильний формат дати"),
    dateOfStart: z
        .iso
        .date("Неправильний формат дати"),
    phoneNumber: z
        .string()
        .regex(phoneRegex, "Неправильний формат номеру телефону"),
    city: z
        .string()
        .min(1, "Місто занадто коротке")
        .max(50, "Місто занадто довге"),
    street: z
        .string()
        .min(1, "Назва вулиці занадто коротка")
        .max(50, "Назва вулиці занадто довга"),
    zipCode: z
        .string()
        .min(3, "Індекс занадто короткий")
        .max(9, "Індекс занадто довгий"),
})

export const EmployeeSchema = BaseEmployeeSchema.refine(
    (data) => new Date(data.dateOfBirth) < new Date(data.dateOfStart), {
        message: "Дата народження має бути раніше ніж дата початку роботи",
        path: ["dateOfStart"]
    }
).refine(
    (data) => new Date(data.dateOfBirth).getFullYear() > 1900, {
        message: "Дата має бути пізніше за 1900",
        path: ["dateOfBirth"]
    }
).refine(
    (data) => new Date(data.dateOfStart) < new Date(), {
        message: "Дата старту має бути в минулому",
        path: ["dateOfStart"]
    }
).refine(
    (data) => new Date().getFullYear() - new Date(data.dateOfBirth).getFullYear() >= 18, {
        message: "Вік працівника_ці має бути більше за 18 років",
        path: ["dateOfBirth"]
    }
).refine(
    (data) => new Date(data.dateOfStart).getFullYear() > 1900, {
        message: "Дата має бути пізніше за 1900 рік",
        path: ["dateOfStart"]
    }
);

export type Employee = z.infer<typeof EmployeeSchema>;

export const CreateEmployeeSchema = EmployeeSchema.extend({
    password: z
        .string()
        .min(8, "Пароль занадто короткий")
        .max(100, "Пароль занадто довгий"),
    repeatPassword: z
        .string()
        .min(8, "Пароль занадто короткий")
        .max(100, "Пароль занадто довгий"),
}).refine(
    (data) => data.password === data.repeatPassword,
    {
        message: "Паролі не збігаються",
        path: ["repeatPassword"]
    }
)

export type CreateEmployee = z.infer<typeof CreateEmployeeSchema>;

export const PageEmployeeSchema = z.object({
    content: z.array(EmployeeSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export const EmployeeContactSchema = z.object({
    idEmployee: z.string(),
    phoneNumber: z.string(),
    city: z.string(),
    street: z.string(),
    zipCode: z.string(),
});

export type EmployeeContact = z.infer<typeof EmployeeContactSchema>;

export const PageEmployeeContactSchema = z.object({
    content: z.array(EmployeeContactSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export type PageResponse<T> = z.infer<typeof PageEmployeeSchema>;

export type PageResponseContact<T> = z.infer<typeof PageEmployeeContactSchema>;
