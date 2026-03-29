import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCustomerCard, deleteCustomerCard, downloadCustomerCardPdf,
    getCustomerCard, getCustomerCardIDList,
    listCustomerCard,
    updateCustomerCard
} from "@/features/customer-card/api/customerCardApi.ts";
import {toast} from "sonner";
import {AxiosError, isAxiosError} from "axios";
import {staleTime} from "@/constants/constants.ts";

const QUERY_KEY = "customer_cards"

export const useDownloadCustomerCardPdf = () => {
    return useMutation({
        mutationFn: downloadCustomerCardPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => alert(error),
    });
};

export const useCreateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Успішно створено картку клієнта!")
        },
        onError: (error) => {
            if (isAxiosError(error) && error.response?.data) {
                if (error.response.status === 400) {
                    toast.error("Картка з таким ID вже існує!");
                    return;
                }
                toast.error("Помилка під час створення картки клієнта")
                return;
            }
            toast.error("Помилка підключення до сервера");
            console.error(error);
        }
    })
}

export const useUpdateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Успішно оновлено картку клієнта!")
        },
        onError: (error) => {
            toast.error("Помилка під час оновлення картки клієнта")
            console.log(error)
        }
    })
}

export const useCustomerCard = (customerCardNumber: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, customerCardNumber],
        queryFn: () => getCustomerCard(customerCardNumber),
        staleTime: staleTime,
        retry: (failureCount, error) => {
            if (error instanceof AxiosError && error.response?.status === 404) {
                return false;
            }
            return failureCount < 3;
        }
    })
}

export const useCustomerCardList = (
    cardNumber: string | undefined = undefined,
    cardSurname: string | undefined = undefined,
    sorted: boolean | undefined = undefined,
    percent: number | undefined = undefined,
    search_surname: string | undefined = undefined,
) => {
    const effectiveSurname = (sorted || percent !== undefined) ? cardSurname : undefined;
    return useQuery({
        queryKey: [QUERY_KEY, cardNumber, effectiveSurname, percent, search_surname, sorted],
        queryFn: () => listCustomerCard(cardNumber, effectiveSurname, percent, search_surname, sorted),
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
            if (isAxiosError(error) && error.response?.data) {
                if (error.response.status === 400) {
                    toast.error("Картка використовується!");
                    return;
                }
                toast.error("Помилка під час видалення картки клієнта")
                return;
            }
            toast.error("Помилка підключення до сервера");
            console.error(error);
        }
    });
}

export const useCustomerCardIDList = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "id-list"],
        queryFn: () => getCustomerCardIDList(),
        staleTime: staleTime,
    })
}