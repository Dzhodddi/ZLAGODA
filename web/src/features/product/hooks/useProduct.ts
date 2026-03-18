import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createProduct,
    updateProduct,
    getAllProducts,
    deleteProduct,
    downloadProductPdf,
    getDeletedProducts, getProduct,
} from "@/features/product/api/productApi";
import type {CreateProduct} from "@/features/product/types/types.ts";

const QUERY_KEY = "products";

export const useProduct = (id: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, id],
        queryFn: () => getProduct(id),
        enabled: !!id,
        staleTime: 1000 * 30,
    });
};

export const useDeletedProducts = (checkNumber: string, page: number, enabled = true) => {
    return useQuery({
        queryKey: ["deleted", checkNumber, page],
        queryFn: () => getDeletedProducts(checkNumber, page),
        enabled: !!checkNumber && enabled,
        staleTime: 1000 * 30,
    });
};

export const useProducts = (name?: string, categoryId?: number, page = 0) => {
    return useQuery({
        queryKey: [QUERY_KEY, name, categoryId, page],
        queryFn: () => getAllProducts(name, categoryId, page),
        staleTime: 1000 * 30,
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
