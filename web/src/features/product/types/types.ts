import {z} from 'zod';

export const BaseProductSchema = z.object({
    idProduct: z
        .coerce
        .number("Некоректне число")
        .min(0, "Число має бути додатним")
        .max(999999, "Число занадто велике"),
    categoryNumber: z
        .coerce
        .number()
        .min(1, "Номер категорії має бути позитивним")
        .max(999999, "Номер категорії завеликий"),
    categoryName: z
        .string()
        .nullable()
        .optional()
        .default(""),
    producer: z
        .string()
        .min(1, "Ім'я виробника занадто коротке")
        .max(50, "Ім'я виробника занадто довге"),
    productName: z
        .string()
        .min(1, "Назва занадто коротка")
        .max(50, "Назва занадто довга"),
    productCharacteristics: z
        .string()
        .min(1, "Характеристики занадто короткі")
        .max(100, "Характеристики занадто довгі"),
});

export const CreateProductSchema = BaseProductSchema
    .omit({
        idProduct: true,
        categoryName: true,
    });

export type CreateProduct = z.infer<typeof CreateProductSchema>;

export type Product = z.infer<typeof BaseProductSchema>;

export const PageProductSchema = z.object({
    content: z.array(BaseProductSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export type PageProduct = z.infer<typeof PageProductSchema>;

export const SoldProductSchema = z.object({
    idProduct: z
        .coerce
        .number(),
    productName: z
        .string(),
    soldNumber: z.coerce.number(),
    totalSold: z.coerce.number(),
});

export const PageSoldProductSchema = z.object({
    content: z.array(SoldProductSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export type PageSoldProduct = z.infer<typeof PageSoldProductSchema>;
