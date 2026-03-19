import {z} from 'zod';

export  const LoginSchema = z.object({
    idEmployee: z
        .string()
        .min(1, "ID занадто коротке")
        .max(50, "ID занадто довге"),
    password: z
        .string()
        .min(1, "Пароль занадто короткий")
        .max(50, "Пароль занадто довгий"),
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
