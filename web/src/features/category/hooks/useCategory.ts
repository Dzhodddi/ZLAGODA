import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCategory,
    deleteCategory, getAllCategories, getCategory, getTopCategories,
    listCategories,
    updateCategory
} from "@/features/category/api/categoryApi.ts";
import {toast} from "sonner";
import {AxiosError, isAxiosError} from "axios";
import {staleTime} from "@/constants/constants.ts";

const QUERY_KEY = "categories"

export const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Успішно створено категорію");
        },
        onError: (error) => {
            toast.error("Помилка під час створення категорії");
            console.error(error);
        }
    })
}

export const useUpdateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Успішно оновлено категорію")
        },
        onError: (error) => {
            toast.error("Помилка під час оновлення категорії")
            console.error(error);
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
            if (isAxiosError(error) && error.response?.data) {
                if (error.response.status === 400) {
                    toast.error("Категорія використовується!");
                    return;
                }
                toast.error("Помилка під час видалення категорії")
                return;
            }
            toast.error("Помилка підключення до сервера");
            console.error(error);
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
