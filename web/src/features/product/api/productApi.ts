import { javaApiClient } from "@/lib/axios";
import {
    type Product,
    type CreateProduct,
    type PageProduct,
    BaseProductSchema,
    PageProductSchema,
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
    page = 0
): Promise<PageProduct> => {
    console.log("getAllProducts params:", { name, categoryId, page });
    const response = await javaApiClient.get(prefix, {
        params: { name, categoryId, page },
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

export const getDeletedProducts = async (
    checkNumber?: string,
    page = 0
): Promise<PageProduct> => {
    const response = await javaApiClient.get(`${prefix}/deleted`, {
        params: { checkNumber, page },
    });
    return PageProductSchema.parse(response.data);
};
