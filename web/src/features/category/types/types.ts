import { z } from 'zod';

export const CategorySchema = z.object({
    categoryNumber: z
        .coerce
        .number("Некоректне число")
        .min(1, "Число має бути додатнім")
        .max(999999, "Число занадто велике"),
    categoryName: z
        .string()
        .min(1, "Назва занадто коротка")
        .max(50, "Назва занадто довга"),
});

export type Category = z.infer<typeof CategorySchema>;

export const CreateCategorySchema = CategorySchema.omit({
    categoryNumber: true
});

export type CreateCategory = z.infer<typeof CreateCategorySchema>;

export const TopCategorySchema = CategorySchema.extend({
    totalSold: z.number()
});

export type TopCategory = z.infer<typeof TopCategorySchema>;
