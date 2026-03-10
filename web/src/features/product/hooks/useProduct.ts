import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createProduct,
    updateProduct,
    getAllProducts,
    deleteProduct,
    downloadProductPdf,
} from "@/features/product/api/productApi";

const QUERY_KEY = "products";

export const useCreateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully created product!");
        },
        onError: (error) => alert(error),
    });
};

export const useUpdateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: Parameters<typeof updateProduct>[1] }) =>
            updateProduct(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully updated product!");
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
            alert("Successfully deleted product!");
        },
        onError: (error) => alert(error),
    });
};

export const useProducts = (name?: string, categoryId?: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, name, categoryId],
        queryFn: () => getAllProducts(name, categoryId),
        staleTime: 1000 * 30,
    });
};

export const useDownloadProductPdf = () => {
    return useMutation({
        mutationFn: downloadProductPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "products.pdf";
            a.click();
            URL.revokeObjectURL(url);
        },
        onError: (error) => alert(error),
    });
};
