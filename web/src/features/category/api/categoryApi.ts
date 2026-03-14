import {goApiClient} from "@/lib/axios.ts";
import {type Category, CategorySchema, type CreateCategory} from "@/features/category/types/types.ts";
import {z} from "zod";

const prefix = "/categories"

export const createCategory = async (data: CreateCategory): Promise<Category> => {
    const response = await goApiClient.post(prefix, data);
    return CategorySchema.parse(response.data);
}

export const updateCategory = async (data: Category): Promise<Category> => {
    const response = await goApiClient.put(`${prefix}/${data.categoryNumber}`, data);
    return CategorySchema.parse(response.data);
}

export const getCategory = async (categoryNumber: number): Promise<Category> => {
    const response = await goApiClient.get(prefix + "/" + categoryNumber);
    return CategorySchema.parse(response.data);
}

export const deleteCategory = async (categoryNumber: number): Promise<void> => {
    await goApiClient.delete(`${prefix}/${categoryNumber}`);
}

export const listCategories = async (
    category_number: number,
    category_name: string | undefined = undefined,
    sorted: boolean | undefined = undefined
): Promise<Category[]> => {
    const response = await goApiClient.get(prefix, {
        params: {category_number, category_name, sorted}
    });
    if (!response.data)
        return []
    return z.array(CategorySchema).parse(response.data);
}