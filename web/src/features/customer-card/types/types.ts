import {z} from 'zod';
import {phoneRegex} from "@/constants/constants.ts";

export const CustomerCardSchema = z.object({
    cardNumber: z
        .string()
        .min(1, "Номер карти занадто короткий")
        .max(13, "Номер карти занадто довгий"),
    customerName: z
        .string()
        .min(1, "Ім'я занадто коротке")
        .max(50, "Ім'я занадто довге"),
    customerSurname: z
        .string()
        .min(1, "Прізвище занадто коротке")
        .max(50, "Прізвище занадто довге"),
    customerPatronymic: z
        .string()
        .max(50, "По батькові занадто довге")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    phoneNumber: z
        .string()
        .min(10, "Номер телефону занадто короткий")
        .max(13, "Номер телефону занадто довгий")
        .regex(phoneRegex, "Неправильний формат номеру телефону"),
    city: z
        .string()
        .max(50, "Місто занадто довге")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    street: z
        .string()
        .max(50, "Назва вулиці занадто довга")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    zipCode: z
        .string()
        .max(9, "Індекс занадто довгий")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    customerPercent: z
        .coerce
        .number("Введіть число")
        .min(1, "Відсоток має бути між 1 і 100")
        .max(100, "Відсоток має бути між 1 і 100"),
});

export type CustomerCard = z.infer<typeof CustomerCardSchema>;

export const CreateCustomerCardSchema = CustomerCardSchema

export type CreateCustomerCard = z.infer<typeof CreateCustomerCardSchema>;

export const CustomerCardDropdownItemSchema = z.object({
    cardNumber: z.string(),
    fullName: z.string(),
})

export type CustomerCardDropdownItem = z.infer<typeof CustomerCardDropdownItemSchema>