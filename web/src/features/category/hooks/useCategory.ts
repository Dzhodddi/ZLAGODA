import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCategory,
    deleteCategory, downloadCategoryPdf, getAllCategories, getCategory, getTopCategories,
    listCategories,
    updateCategory
} from "@/features/category/api/categoryApi.ts";
import {toast} from "sonner";
import {AxiosError} from "axios";
import {staleTime} from "@/constants/constants.ts";
import {getErrorMessage} from "@/lib/errorUtils.ts";

const QUERY_KEY = "categories"

export const useDownloadCategoryPdf = () => {
    return useMutation({
        mutationFn: downloadCategoryPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося відкрити звіт"));
        },
    });
};

export const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Категорія успішно створена");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити категорію"));
        }
    })
}

export const useUpdateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Категорія успішно оновлена")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося оновити категорію"))
        }
    })
}

export const useCategoryList = (
    cursor: number,
    name: string | undefined = undefined,
    sorted: boolean | undefined = undefined
) => {
    return useQuery({
        queryKey: [QUERY_KEY, cursor, sorted],
        queryFn: () => listCategories(cursor, name, sorted),
        placeholderData: (previousData) => previousData,
    });
}

export const useDeleteCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося видалити категорію"))
        }
    });
};

export const useCategory = (categoryNumber: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, categoryNumber],
        queryFn: () => getCategory(categoryNumber),
        enabled: !!categoryNumber,
        staleTime: staleTime,
        retry: (failureCount, error) => {
            if (error instanceof AxiosError && error.response?.status === 404) {
                return false;
            }
            return failureCount < 3;
        }
    });
};

export const useTopCategories = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "top"],
        queryFn: () => getTopCategories(),
        staleTime: staleTime,
    });
};

export const useAllCategories = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "all"],
        queryFn: () => getAllCategories(),
        staleTime: staleTime,
    });
};
