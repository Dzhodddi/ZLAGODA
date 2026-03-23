import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createProduct,
    updateProduct,
    getAllProducts,
    deleteProduct,
    downloadProductPdf,
    getSoldProducts,
    getProduct,
} from "@/features/product/api/productApi";
import type {CreateProduct} from "@/features/product/types/types.ts";
import {staleTime} from "@/constants/constants.ts";

const QUERY_KEY = "products";

export const useProduct = (id: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, id],
        queryFn: () => getProduct(id),
        enabled: !!id,
        staleTime: staleTime,
    });
};

export const useSoldProducts = (page: number, enabled = true) => {
    return useQuery({
        queryKey: ["sold", page],
        queryFn: () => getSoldProducts(page),
        staleTime: staleTime,
    });
};

export const useProducts = (name?: string, categoryId?: number, page = 0) => {
    return useQuery({
        queryKey: [QUERY_KEY, name, categoryId, page],
        queryFn: () => getAllProducts(name, categoryId, page),
        staleTime: staleTime,
    });
};

export const useCreateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => alert(error),
    });
};

export const useUpdateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, ...data }: CreateProduct & { id: number }) =>
            updateProduct(id, data as CreateProduct),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => alert(error),
    });
};

export const useDeleteProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => alert(error),
    });
};

export const useDownloadProductPdf = () => {
    return useMutation({
        mutationFn: downloadProductPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const win = window.open(url);
            win?.print();
            URL.revokeObjectURL(url);
        },
        onError: (error) => alert(error),
    });
};

export const useAllProducts = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "all"],
        queryFn: async () => {
            const results = [];
            let page = 0;
            let hasNext = true;
            while (hasNext) {
                const data = await getAllProducts(undefined, undefined, page);
                results.push(...data.content);
                hasNext = data.hasNext;
                page++;
            }
            return results;
        },
        staleTime: staleTime,
    });
};
