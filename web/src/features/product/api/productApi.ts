import { javaApiClient } from "@/lib/axios";
import {
    type Product,
    ProductSchema,
    type CreateProduct,
    PageProductSchema,
} from "@/features/product/types/types";

const prefix = "/products";

export interface PageResponse<T> {
    content: T[];
    pageSize: number;
    totalElements: number;
    hasNext: boolean;
}

export const createProduct = async (data: CreateProduct): Promise<Product> => {
    const response = await javaApiClient.post(prefix, data);
    return ProductSchema.parse(response.data);
};

export const updateProduct = async (idProduct: number, data: CreateProduct): Promise<Product> => {
    const response = await javaApiClient.put(prefix + "/" + idProduct, data);
    return ProductSchema.parse(response.data);
};

export const getAllProducts = async (
    name?: string,
    categoryId?: number,
    lastSeenId?: number
): Promise<PageResponse<Product>> => {
    const response = await javaApiClient.get(prefix, {
        params: { name, categoryId, lastSeenId },
    });
    return PageProductSchema.parse(response.data);
};

export const deleteProduct = async (idProduct: number): Promise<void> => {
    await javaApiClient.delete(prefix + "/" + idProduct);
};

export const downloadProductPdf = async (): Promise<Blob> => {
    const response = await javaApiClient.get(prefix + "/report", {
        responseType: "blob",
    });
    return response.data;
};
