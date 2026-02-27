import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {createCustomerCard, getCustomerCard} from "@/features/customer-card/api/customerCardApi.ts";


export const useCreateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['customer_cards']})
            alert("Successfully created customer card!")
        },
        onError: (error) => {
            alert(error)
        }
    })
}

export const useCustomerCard = (customerCardNumber: string) => {
    return useQuery({
        queryKey: ['customer_cards', customerCardNumber],
        queryFn: () => getCustomerCard(customerCardNumber),
        staleTime: 1000 * 30 // 30 sec
    })
}