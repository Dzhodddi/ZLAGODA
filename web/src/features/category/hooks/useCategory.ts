import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {createCategory, getCategory} from "@/features/category/api/categoryApi.ts";


export const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCategory,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['categories']})
            alert("Successfully created category!")
        },
        onError: (error) => {
            alert(error)
        }
    })
}

export const useCategory = (category_number: string) => {
    return useQuery({
        queryKey: ['categories', category_number],
        queryFn: () => getCategory(category_number),
        staleTime: 1000 * 30 // 30 sec
    })
}