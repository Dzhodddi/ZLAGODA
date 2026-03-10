import {javaApiClient} from "@/lib/axios.ts";
import {
    type StoreProduct,
    StoreProductSchema,
    type CreateStoreProduct,
    PageStoreProductSchema,
    type StoreProductPriceAndQuantity,
    StoreProductPriceAndQuantitySchema
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
    return StoreProductSchema.parse(response.data);
}

export const updateStoreProduct = async (upc: string, data: CreateStoreProduct): Promise<StoreProduct> => {
    const response = await javaApiClient.put(prefix + "/" + upc, data);
    return StoreProductSchema.parse(response.data);
}

export const getAllStoreProducts = async (
    sortedBy?: "name" | "quantity",
    prom?: boolean,
    lastSeenUPC?: string
): Promise<PageResponse<StoreProduct>> => {
    const response = await javaApiClient.get(prefix, {
        params: { sortedBy, prom, lastSeenUPC }
    });
    return PageStoreProductSchema.parse(response.data);
};

export const deleteStoreProduct = async (upc: string): Promise<void> => {
    await javaApiClient.delete(prefix + "/" + upc);
}

export const getStoreProduct = async (
    upc: string,
    selling_price: boolean = true,
    quantity: boolean = true,
    name: boolean = true,
    characteristics: boolean = true,
): Promise<StoreProduct> => {
    const response = await javaApiClient.get(prefix + "/" + upc, {
        params: { selling_price, quantity, name, characteristics }
    });
    return StoreProductSchema.parse(response.data);
}

export const getStoreProductPriceAndQuantity = async (
    upc: string,
    selling_price: boolean = true,
    quantity: boolean = true,
    name: boolean = false,
    characteristics: boolean = false,
): Promise<StoreProductPriceAndQuantity> => {
    const response = await javaApiClient.get(prefix + "/" + upc, {
        params: { selling_price, quantity, name, characteristics }
    });
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
