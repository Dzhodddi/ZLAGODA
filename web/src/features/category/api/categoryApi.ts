import {goApiClient} from "@/lib/axios.ts";
import {type Category, CategorySchema, type CreateCategory} from "@/features/category/types/types.ts";

const prefix = "/categories"

export const createCategory = async (data: CreateCategory): Promise<Category> => {
    const response = await goApiClient.post(prefix, data);
    return CategorySchema.parse(response.data);
}

export const updateCategory = async (data: CreateCategory): Promise<Category> => {
    const response = await goApiClient.put(prefix, data);
    return CategorySchema.parse(response.data);
}

export const getCategory = async (categoryNumber: string): Promise<Category> => {
    const response = await goApiClient.get(prefix + "/" + categoryNumber);
    return CategorySchema.parse(response.data);
}

export const deleteCategory = async (categoryNumber: string): Promise<void> => {
    await goApiClient.delete(prefix + "/" + categoryNumber);
}
