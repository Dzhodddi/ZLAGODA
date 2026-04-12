import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createProduct,
    updateProduct,
    getAllProducts,
    deleteProduct,
    downloadProductPdf,
    getSoldProducts,
    getProduct,
    getProductSoldNumber,
} from "@/features/product/api/productApi";
import type {CreateProduct} from "@/features/product/types/types.ts";
import {staleTime} from "@/constants/constants.ts";
import {toast} from "sonner";
import {getErrorMessage} from "@/lib/errorUtils.ts";
import {AxiosError} from "axios";

const QUERY_KEY = "products";

export const useProduct = (id: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, id],
        queryFn: () => getProduct(id),
        enabled: !!id,
        staleTime: staleTime,
        retry: (failureCount, error) => {
            if (error instanceof AxiosError && error.response?.status === 404) {
                return false;
            }
            return failureCount < 3;
        }
    });
};

export const useProductSoldNumber = (idProduct: number,
                                     startDate: string,
                                     endDate: string,
                                     isEnabled: boolean) => {
    return useQuery({
        queryKey: ["sold-number", idProduct, startDate, endDate],
        queryFn: () => getProductSoldNumber(idProduct, startDate, endDate),
        enabled: isEnabled,
        staleTime: staleTime,
    });
};

export const useSoldProducts = (page: number, minTotalSold: number | undefined) => {
    return useQuery({
        queryKey: ["sold", page, minTotalSold],
        queryFn: () => getSoldProducts(page, minTotalSold),
        staleTime: staleTime,
    });
};

export const useProducts = (name?: string, categoryId?: number, page = 0, sortedByName = false) => {
    return useQuery({
        queryKey: [QUERY_KEY, name, categoryId, page, sortedByName],
        queryFn: () => getAllProducts(name, categoryId, page, sortedByName),
        staleTime: staleTime,
    });
};

export const useCreateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Товар успішно створений");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити товар"));
        }
    });
};

export const useUpdateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, ...data }: CreateProduct & { id: number }) =>
            updateProduct(id, data as CreateProduct),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Товар успішно оновлений")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося оновити товар"));
        }
    });
};

export const useDeleteProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteProduct,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося видалити товар"));
        }
    });
};

export const useDownloadProductPdf = () => {
    return useMutation({
        mutationFn: downloadProductPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const newTab = window.open("", "_blank");
            if (newTab) {
                newTab.document.write(`
                    <!DOCTYPE html>
                    <html>
                    <head><title>products.pdf</title></head>
                    <body style="margin:0">
                        <iframe 
                            src="${url}" 
                            style="width:100vw; height:100vh; border:none"
                            onload="this.contentWindow.print()"
                        ></iframe>
                    </body>
                    </html>
                `);
                newTab.document.close();
            }
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося відкрити звіт"));
        },
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
                const data = await getAllProducts(undefined, undefined, page, undefined);
                results.push(...data.content);
                hasNext = data.hasNext;
                page++;
            }
            return results;
        },
        staleTime: staleTime,
    });
};
