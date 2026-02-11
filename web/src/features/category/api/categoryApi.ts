import {goApiClient} from "@/lib/axios.ts";
import {type Category, CategorySchema, type CreateCategory} from "@/features/category/types/types.ts";

const prefix = "/categories"

export const createCategory = async (data: CreateCategory): Promise<Category> => {
    const response = await goApiClient.post(prefix, data);
    return CategorySchema.parse(response.data);
}

export const getCategory = async (category_number: string): Promise<Category> => {
    const response = await goApiClient.get(prefix + "/" + category_number);
    return CategorySchema.parse(response.data);
}