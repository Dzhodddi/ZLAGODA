import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCategory,
    deleteCategory, getAllCategories, getCategory, getTopCategories,
    listCategories,
    updateCategory
} from "@/features/category/api/categoryApi.ts";
import {toast} from "sonner";

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
            toast.error("Помилка під час видалення категорії")
            console.error(error)
        }
    });
};

export const useCategory = (categoryNumber: number) => {
    return useQuery({
        queryKey: [QUERY_KEY, categoryNumber],
        queryFn: () => getCategory(categoryNumber),
        enabled: !!categoryNumber,
        staleTime: 1000 * 30,
    });
};

export const useTopCategories = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "top"],
        queryFn: () => getTopCategories(),
        staleTime: 1000 * 30,
    });
};

export const useAllCategories = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "all"],
        queryFn: () => getAllCategories(),
        staleTime: 1000 * 30,
    });
};
