import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createStoreProduct,
    updateStoreProduct,
    getAllStoreProducts,
    deleteStoreProduct,
    getStoreProduct,
    getStoreProductPriceAndQuantity,
    deleteExpired,
    downloadStoreProductPdf, receiveNewBatch, getStoreProductsList
} from "@/features/store_product/api/storeProductApi.ts";
import {staleTime} from "@/constants/constants.ts";
import type {BatchRequest, CreateStoreProduct} from "@/features/store_product/types/types.ts";
import {toast} from "sonner";
import {getErrorMessage} from "@/lib/errorUtils.ts";

const QUERY_KEY = "store-products";

export const useCreateStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createStoreProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Товар у магазині успішно створений");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити товар у магазині"));
        }
    });
};

export const useUpdateStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateStoreProduct & { upc: string }) =>
            updateStoreProduct(payload.upc, payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Товар у магазині успішно оновлений")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося оновити товар у магазині"))
        }
    });
};

export const useDeleteStoreProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteStoreProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося видалити товар у магазині"))
        }
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
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося відкрити звіт"));
        },
    });
};

export const useDeleteExpired = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: () => deleteExpired(),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося видалити протерміновані товари у магазині"));
        },
    });
};

export const useReceiveNewBatch = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: BatchRequest) => receiveNewBatch(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Партія товарів успішно створена")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити партію товарів"))
        }
    });
};

export const useStoreProductsList = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "list"],
        queryFn: () => getStoreProductsList(),
    });
};