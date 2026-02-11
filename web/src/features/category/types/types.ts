import {z} from 'zod';

export const CategorySchema = z.object({
    categoryNumber: z.number(),
    categoryName: z.string().min(1).max(50),
})

export type Category = z.infer<typeof CategorySchema>

export const CreateCategorySchema = CategorySchema.omit({
    categoryNumber: true
});

export type CreateCategory = z.infer<typeof CreateCategorySchema>;