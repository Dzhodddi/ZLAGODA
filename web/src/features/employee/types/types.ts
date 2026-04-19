import {z} from 'zod';
import {phoneRegex} from "@/constants/constants.ts";

export const EmployeeSchema = z.object({
    idEmployee: z.string().min(1, "ID занадто короткий").max(10, "ID занадто довгий"),
    emplSurname: z.string().min(1, "Прізвище занадто коротке").max(50, "Прізвище занадто довге"),
    emplName: z.string().min(1, "Ім'я занадто коротке").max(50, "Ім'я занадто довге"),
    emplPatronymic: z.string().max(50, "По батькові занадто довге").nullable().optional().transform((val) => val?.trim() === "" || val === undefined ? null : val),
    role: z.enum(["MANAGER", "CASHIER"]),
    salary: z.preprocess(
        (val) => (val === "" || val === null || val === undefined ? undefined : val),
        z.coerce.number({ message: "Введіть число" })
            .min(0, "Зарплата не може набувати від'ємних значень")
            .max(999999999.9999, "Зарплата завелика")
    ),

    dateOfBirth: z
        .string()
        .refine((val) => new Date(val).getFullYear() > 1900, "Дата має бути пізнішою за 1900")
        .refine((val) => new Date().getFullYear() - new Date(val).getFullYear() >= 18, "Вік працівника має бути більшим за 18 років"),

    dateOfStart: z
        .string()
        .refine((val) => new Date(val).getFullYear() > 1900, "Дата має бути пізнішою за 1900 рік")
        .refine((val) => new Date(val) < new Date(), "Дата початку роботи має бути в минулому"),

    phoneNumber: z.string().regex(phoneRegex, "Неправильний формат номеру телефону"),
    city: z.string().min(1, "Назва міста занадто коротка").max(50, "Назва міста занадто довга"),
    street: z.string().min(1, "Назва вулиці занадто коротка").max(50, "Назва вулиці занадто довга"),
    zipCode: z.string().min(3, "Індекс занадто короткий").max(9, "Індекс занадто довгий"),
});

export type Employee = z.infer<typeof EmployeeSchema>;

export const CreateEmployeeSchema = EmployeeSchema
    .extend({
        password: z
            .string()
            .min(8, "Пароль занадто короткий")
            .max(20, "Пароль занадто довгий"),
        repeatPassword: z
            .string()
            .min(8, "Пароль занадто короткий")
            .max(20, "Пароль занадто довгий"),
    })
    .refine(
    (data) => data.password === data.repeatPassword,
    {
        message: "Паролі не збігаються",
        path: ["repeatPassword"]
    })
    .refine(
        (data) => (new Date(data.dateOfStart).valueOf() -  new Date(data.dateOfBirth).valueOf()) / (1000 * 60 * 60 * 24 * 365.25) >= 18,
        {
            message: "На момент початку роботи працівнику має бути 18 років",
            path: ["dateOfStart"]
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
    emplSurname: z.string(),
    emplName: z.string(),
    emplPatronymic: z.string().nullish(),
    phoneNumber: z.string(),
    city: z.string(),
    street: z.string(),
    zipCode: z.string(),
});

export const PageEmployeeContactSchema = z.object({
    content: z.array(EmployeeContactSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export const EmployeeDropdownItemSchema = z.object({
    idEmployee: z.string(),
    fullName: z.string(),
})

export type EmployeeDropdownItem = z.infer<typeof EmployeeDropdownItemSchema>