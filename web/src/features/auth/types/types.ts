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