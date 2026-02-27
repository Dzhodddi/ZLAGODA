import {z} from 'zod';

const phoneRegex = new RegExp(/^([0-9]{10,13})$/);

export const CustomerCardSchema = z.object({
    cardNumber: z
        .string()
        .min(1, "Card number too short")
        .max(13, "Card number too long"),
    customerName: z
        .string()
        .min(1, "Name too short")
        .max(50, "Name too long"),
    customerSurname: z
        .string()
        .min(1, "Surname too short")
        .max(50, "Surname too long"),
    customerPatronymic: z.
        string().
        max(50, "Patronymic too long")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),

    phoneNumber: z
        .string().
        min(10, "Phone number too short").
        max(13, "Phone number too long").
        regex(phoneRegex, "Invalid phone number"),
    city: z.
        string().
        max(50, "Patronymic too long")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    street: z.
        string().
        max(50, "Patronymic too long")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    zipCode: z.
        string().
        max(9, "Patronymic too long")
        .nullable()
        .optional()
        .transform((val) => val === "" || val === undefined ? null : val),
    customerPercent: z
        .coerce
        .number("Invalid number")
        .min(1, "Percent must be between 1 and 100")
        .max(100, "Percent must be between 1 and 100"),
});

export type CustomerCard = z.infer<typeof CustomerCardSchema>;

export const CreateCustomerCardSchema = CustomerCardSchema

export type CreateCustomerCard = z.infer<typeof CreateCustomerCardSchema>;
