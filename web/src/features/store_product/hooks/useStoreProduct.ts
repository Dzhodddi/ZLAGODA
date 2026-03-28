import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createStoreProduct,
    updateStoreProduct,
    getAllStoreProducts,
    deleteStoreProduct,
    getStoreProduct,
    getStoreProductPriceAndQuantity,
    deleteExpired,
    downloadStoreProductPdf, receiveNewBatch
} from "@/features/store_product/api/storeProductApi.ts";
import {staleTime} from "@/constants/constants.ts";
import type {BatchRequest, CreateStoreProduct} from "@/features/store_product/types/types.ts";

const QUERY_KEY = "store-products";

export const useCreateStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createStoreProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully created store product!");
        },
        onError: (error) => alert(error),
    });
};

export const useUpdateStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateStoreProduct & { upc: string }) =>
            updateStoreProduct(payload.upc, payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
    });
};

export const useDeleteStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteStoreProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully deleted store product!");
        },
        onError: (error) => alert(error),
    });
};

export const useAllStoreProducts = (
    {sortedBy, prom, page, enabled = true}: {
        sortedBy?: "name" | "quantity",
        prom?: boolean,
        page: number,
        enabled?: boolean
    }
) => {
    return useQuery({
        queryKey: [QUERY_KEY, sortedBy, prom, page],
        queryFn: () => getAllStoreProducts(sortedBy, prom, page),
        enabled,
        staleTime: staleTime,
    });
};

export const useStoreProduct = (upc: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, upc],
        queryFn: () => getStoreProduct(upc),
        enabled: !!upc,
        staleTime: staleTime,
    });
};

export const useStoreProductPriceAndQuantity = (upc: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, upc, "price-quantity"],
        queryFn: () => getStoreProductPriceAndQuantity(upc),
        enabled: !!upc,
        staleTime: staleTime,
    });
};

export const useDownloadStoreProductPdf = () => {
    return useMutation({
        mutationFn: downloadStoreProductPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => alert(error),
    });
};

export const useDeleteExpired = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: () => deleteExpired(),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => alert(error),
    });
};

export const useReceiveNewBatch = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: BatchRequest) => receiveNewBatch(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error: any) => {
            console.error("Mutation error:", error);
            alert("Помилка при збереженні партії");
        }
    });
};
