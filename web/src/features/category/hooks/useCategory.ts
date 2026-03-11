import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCategory,
    deleteCategory,
    listCategories,
    updateCategory
} from "@/features/category/api/categoryApi.ts";
import {toast} from "sonner";


export const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['categories']})
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
            queryClient.invalidateQueries({queryKey: ['categories']})
            toast.success("Успішно оновлено категорію")
        },
        onError: (error) => {
            toast.error("Помилка під час оновлення категорії")
            console.error(error);
        }
    })
}

export const useCategoryList = () => {
    return useQuery({
        queryKey: ['categories'],
        queryFn: listCategories,
    });
}

export const useDeleteCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['categories'] });
        },
        onError: (error) => {
            toast.error("Помилка під час видалення категорії")
            console.error(error)
        }
    });
};