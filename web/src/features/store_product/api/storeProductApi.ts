import {goApiClient, javaApiClient} from "@/lib/axios.ts";
import {
    type StoreProduct,
    type CreateStoreProduct,
    PageStoreProductSchema,
    type StoreProductPriceAndQuantity,
    StoreProductPriceAndQuantitySchema,
    BaseStoreProductSchema,
    type BatchRequest,
    type BatchDto, type StoreProductItem, StoreProductItemSchema,
} from "@/features/store_product/types/types.ts";

const prefix = "/store-products"

export interface PageResponse<T> {
    content: T[];
    pageSize: number;
    totalElements: number;
    hasNext: boolean;
}

export const createStoreProduct = async (data: CreateStoreProduct): Promise<StoreProduct> => {
    const response = await javaApiClient.post(prefix, data);
    return BaseStoreProductSchema.parse(response.data);
}

export const updateStoreProduct = async (upc: string, data: CreateStoreProduct): Promise<StoreProduct> => {
    const response = await javaApiClient.put(prefix + "/" + upc, data);
    return BaseStoreProductSchema.parse(response.data);
}

export const getAllStoreProducts = async (
    sortedBy?: "name" | "quantity",
    prom?: boolean,
    page = 0
): Promise<PageResponse<StoreProduct>> => {
    const response = await javaApiClient.get(prefix, {
        params: { sortedBy, prom, page }
    });
    return PageStoreProductSchema.parse(response.data);
};

export const deleteStoreProduct = async (upc: string): Promise<void> => {
    await javaApiClient.delete(prefix + "/" + upc);
}

export const getStoreProduct = async (upc: string): Promise<StoreProduct> => {
    const response = await javaApiClient.get(prefix + "/" + upc);
    return BaseStoreProductSchema.parse(response.data);
}

export const getStoreProductPriceAndQuantity = async (
    upc: string,
): Promise<StoreProductPriceAndQuantity> => {
    const response = await javaApiClient.get(prefix + "/" + upc);
    return StoreProductPriceAndQuantitySchema.parse(response.data);
}

export const downloadStoreProductPdf = async (): Promise<Blob> => {
    const response = await javaApiClient.get(prefix + "/report", {
        responseType: "blob",
    });
    return response.data;
}

export const deleteExpired = async (): Promise<void> => {
    await javaApiClient.delete(prefix + "/expired");
}

export const receiveNewBatch = async (data: BatchRequest): Promise<BatchDto> => {
    const response = await javaApiClient.post(prefix + "/receive", data);
    return response.data;
}

export const getStoreProductsList = async (): Promise<StoreProductItem[]> => {
    const response = await goApiClient.get(`${prefix}/list`);
    return StoreProductItemSchema.array().parse(response.data);
};