import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCustomerCard, deleteCustomerCard,
    getCustomerCard,
    listCustomerCard,
    updateCustomerCard
} from "@/features/customer-card/api/customerCardApi.ts";
import {toast} from "sonner";

const QUERY_KEY = "customer_cards"

export const useCreateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            alert("Успішно створено картку клієнта!")
        },
        onError: (error) => {
            alert(error)
        }
    })
}

export const useUpdateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            alert("Успішно оновлено картку клієнта!")
        },
        onError: (error) => {
            alert(error)
        }
    })
}

export const useCustomerCard = (customerCardNumber: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, customerCardNumber],
        queryFn: () => getCustomerCard(customerCardNumber),
        staleTime: 1000 * 30 // 30 sec
    })
}

export const useCustomerCardList = (
    sorted: boolean | undefined = undefined
) => {
    return useQuery({
        queryKey: [QUERY_KEY, sorted],
        queryFn: () => listCustomerCard(sorted),
        placeholderData: (previousData) => previousData,
    });
}

export const useDeleteCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            toast.error("Помилка під час видалення картки клієнта")
            console.error(error)
        }
    });
}