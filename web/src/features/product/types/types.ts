import {z} from 'zod';

export const ProductSchema = z.object({
    productName: z.string(),
    productCharacteristics: z.string(),
});

export type Product = z.infer<typeof ProductSchema>;

export const PageProductSchema = z.object({
    content: z.array(ProductSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export const CreateProductSchema = z.object({
    categoryNumber: z
        .coerce
        .number()
        .min(1, "Номер категорії має бути позитивним")
        .max(999999, "Номер категорії завеликий"),
    productName: z
        .string()
        .min(1, "Назва занадто коротка")
        .max(50, "Назва занадто довга"),
    productCharacteristics: z
        .string()
        .min(1, "Характеристики занадто короткі")
        .max(100, "Характеристики занадто довгі"),
});

export type CreateProduct = z.infer<typeof CreateProductSchema>;
