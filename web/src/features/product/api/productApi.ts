import { javaApiClient } from "@/lib/axios";
import {
    type Product,
    type CreateProduct,
    type PageProduct,
    BaseProductSchema,
    PageProductSchema,
    type PageSoldProduct,
    PageSoldProductSchema,
} from "@/features/product/types/types";

const prefix = "/products";

export const getProduct = async (idProduct: number): Promise<Product> => {
    const response = await javaApiClient.get(`${prefix}/${idProduct}`);
    return BaseProductSchema.parse(response.data);
};

export const createProduct = async (data: CreateProduct): Promise<Product> => {
    const response = await javaApiClient.post(prefix, data);
    return BaseProductSchema.parse(response.data);
};

export const updateProduct = async (idProduct: number, data: CreateProduct): Promise<Product> => {
    const response = await javaApiClient.put(`${prefix}/${idProduct}`, data);
    return BaseProductSchema.parse(response.data);
};

export const getAllProducts = async (
    name?: string,
    categoryId?: number,
    page = 0,
    sortedByName = false
): Promise<PageProduct> => {
    const response = await javaApiClient.get(prefix, {
        params: {
            name,
            category_id: categoryId,
            page,
            ...(sortedByName && { sortedByName: true }),
        },
    });
    return PageProductSchema.parse(response.data);
};

export const deleteProduct = async (idProduct: number): Promise<void> => {
    await javaApiClient.delete(`${prefix}/${idProduct}`);
};

export const downloadProductPdf = async (): Promise<Blob> => {
    const response = await javaApiClient.get(`${prefix}/report`, {
        responseType: "blob",
    });
    return response.data;
};

export const getSoldProducts = async (
    page = 0
): Promise<PageSoldProduct> => {
    const response = await javaApiClient.get(`${prefix}/sold`, {
        params: { page },
    });
    return PageSoldProductSchema.parse(response.data);
};
