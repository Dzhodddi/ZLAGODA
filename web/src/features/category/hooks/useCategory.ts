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
            toast.success("Successfully created category!");
        },
        onError: (error) => {
            toast.error("Failed to create category");
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
            toast.success("Successfully updated category")
        },
        onError: (error) => {
            toast.error("Failed to update category")
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
            toast.error("Could not delete category. It might be in use.")
            console.error(error)
        }
    });
};