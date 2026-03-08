import {z} from 'zod';

export  const LoginSchema = z.object({
    idEmployee: z
        .string()
        .min(1)
        .max(50),
    password: z
        .string()
        .min(1)
        .max(50)
})

export type Login = z.infer<typeof LoginSchema>

export  const LoginResponseSchema = z.object({
    accessToken: z
        .string()
        .min(1),
    refreshToken: z
        .string()
        .min(1)
})

export type LoginResponse = z.infer<typeof LoginResponseSchema>

const phoneRegex = new RegExp(/^([0-9]{10,13})$/);

export const RegisterEmployeeSchema = z.object({
    idEmployee: z
        .string()
        .min(1, "id too short")
        .max(10, "id too long"),
    emplSurname: z
        .string()
        .min(1, "Surname too short")
        .max(50, "Surname too long"),
    emplName: z
        .string()
        .min(1, "Name too short")
        .max(50, "Name too long"),
    emplPatronymic: z
        .string()
        .max(50, "Patronymic too long")
        .nullable()
        .optional()
        .transform((val) => val?.trim() === "" || val === undefined ? null : val),
    role: z
        .string()
        .min(1, "Role too short")
        .max(10, "Role too long"),
    salary: z
        .coerce
        .number()
        .min(1, "Salary must be positive")
        .max(999999999.9999, "Salary too large"),
    dateOfBirth: z
        .coerce
        .date()
        .min(new Date("1900-01-01"), "Date must be after 1900")
        .max(new Date("2008-12-31"), "Age must be at least 18 years"),
    dateOfStart: z
        .coerce
        .date()
        .min(new Date("1900-01-01"), "Date must be after 1900"),
    phoneNumber: z
        .string()
        .regex(phoneRegex, "Invalid phone number"),
    city: z
        .string()
        .min(1, "City too short")
        .max(50, "City too long"),
    street: z
        .string()
        .min(1, "Street too short")
        .max(50, "Street too long"),
    zipCode: z
        .string()
        .min(1, "Street too short")
        .max(9, "Street too long"),
    password: z
        .string()
        .min(1, "Password too short")
        .max(100, "Password too long"),
    repeatPassword: z
        .string()
        .min(1, "Password too short")
        .max(100, "Password too long"),
}).refine(
    (data) => data.dateOfStart.getTime() > data.dateOfBirth.getTime(),
    {
        message: "Date of start must be after date of birth",
        path: ["dateOfStart"]
    }
).refine(
    (data ) => data.password === data.repeatPassword,
    {
        message: "Passwords do not match",
        path: ["repeatPassword"]
    }
);

export type RegisterEmployee = z.infer<typeof RegisterEmployeeSchema>;
