import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createStoreProduct,
    updateStoreProduct,
    getAllStoreProducts,
    deleteStoreProduct,
    getStoreProduct,
    getStoreProductPriceAndQuantity,
    deleteExpired,
    downloadStoreProductPdf
} from "@/features/store_product/api/storeProductApi.ts";

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
        mutationFn: ({ upc, data }: { upc: string; data: Parameters<typeof updateStoreProduct>[1] }) =>
            updateStoreProduct(upc, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully updated store product!");
        },
        onError: (error) => alert(error),
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
    sortedBy?: "name" | "quantity",
    prom?: boolean
) => {
    return useQuery({
        queryKey: [QUERY_KEY, sortedBy, prom],
        queryFn: () => getAllStoreProducts(sortedBy, prom),
        staleTime: 1000 * 30,
    });
};

export const useStoreProduct = (upc: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, upc],
        queryFn: () => getStoreProduct(upc),
        enabled: !!upc,
        staleTime: 1000 * 30,
    });
};

export const useStoreProductPriceAndQuantity = (upc: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, upc, "price-quantity"],
        queryFn: () => getStoreProductPriceAndQuantity(upc),
        enabled: !!upc,
        staleTime: 1000 * 30,
    });
};

export const useDownloadStoreProductPdf = () => {
    return useMutation({
        mutationFn: downloadStoreProductPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const win = window.open(url);
            win?.print();
            URL.revokeObjectURL(url);
        },
        onError: (error) => alert(error),
    });
};

export const useDeleteExpired = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteExpired,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Expired products deleted!");
        },
        onError: (error) => alert(error),
    });
};
