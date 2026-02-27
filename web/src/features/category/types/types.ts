import {z} from 'zod';

export const CategorySchema = z.object({
    categoryNumber: z
        .coerce
        .number("Invalid number")
        .min(1, "Number must be positive")
        .max(999999, "Number too large"),
    categoryName: z
        .string()
        .min(1, "Name too short")
        .max(50, "Name too long"),
})

export type Category = z.infer<typeof CategorySchema>

export const CreateCategorySchema = CategorySchema.omit({
    categoryNumber: true
});

export type CreateCategory = z.infer<typeof CreateCategorySchema>;