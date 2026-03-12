import {z} from 'zod';

const phoneRegex = new RegExp(/^([0-9]{10,13})$/);

export const EmployeeSchema = z.object({
    idEmployee: z
        .string(),
    emplSurname: z
        .string(),
    emplName: z
        .string(),
    emplPatronymic: z
        .string()
        .nullable()
        .optional(),
    role: z
        .enum(["MANAGER", "CASHIER"]),
    salary: z
        .coerce
        .number(),
    dateOfBirth: z
        .string(),
    dateOfStart: z
        .string(),
    phoneNumber: z
        .string(),
    city: z
        .string(),
    street: z
        .string(),
    zipCode: z
        .string(),
});

export type Employee = z.infer<typeof EmployeeSchema>;

export const PageEmployeeSchema = z.object({
    content: z.array(EmployeeSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export const CreateEmployeeSchema = z.object({
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
        .number()
        .min(0, "Зарплата має бути позитивною")
        .max(999999999.9999, "Зарплата завелика"),
    dateOfBirth: z
        .string()
        .min(1, "Вкажіть дату народження"),
    dateOfStart: z
        .string()
        .min(1, "Вкажіть дату прийому"),
    phoneNumber: z
        .string()
        .regex(phoneRegex, "Неправильний формат номеру телефону"),
    city: z
        .string()
        .min(1, "Місто занадто коротке")
        .max(50, "Місто занадто довге"),
    street: z
        .string()
        .min(1, "Вулиця занадто коротка")
        .max(50, "Вулиця занадто довга"),
    zipCode: z
        .string()
        .min(1, "Індекс занадто короткий")
        .max(9, "Індекс занадто довгий"),
    password: z
        .string()
        .max(100, "Пароль занадто довгий")
        .optional()
        .or(z.literal("")),
    repeatPassword: z
        .string()
        .max(100, "Пароль занадто довгий")
        .optional()
        .or(z.literal("")),
}).refine(
    (data) => data.password === data.repeatPassword,
    {
        message: "Паролі не збігаються",
        path: ["repeatPassword"]
    }
);

export type CreateEmployee = z.infer<typeof CreateEmployeeSchema>;

export const EmployeeContactSchema = z.object({
    idEmployee: z
        .string()
        .min(1, "ID занадто короткий")
        .max(10, "ID занадто довгий"),
    phoneNumber: z
        .string()
        .regex(phoneRegex, "Неправильний формат номеру телефону"),
    city: z
        .string()
        .min(1, "Місто занадто коротке")
        .max(50, "Місто занадто довге"),
    street: z
        .string()
        .min(1, "Вулиця занадто коротка")
        .max(50, "Вулиця занадто довга"),
    zipCode: z
        .string()
        .min(1, "Індекс занадто короткий")
        .max(9, "Індекс занадто довгий")
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
