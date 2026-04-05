import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createCustomerCard, deleteCustomerCard, downloadCustomerCardPdf,
    getCustomerCard, getCustomerCardHistory, getCustomerCardIDList,
    listCustomerCard,
    updateCustomerCard
} from "@/features/customer-card/api/customerCardApi.ts";
import {toast} from "sonner";
import {AxiosError, isAxiosError} from "axios";
import {staleTime} from "@/constants/constants.ts";
import {getErrorMessage} from "@/lib/errorUtils.ts";

const QUERY_KEY = "customer_cards"

export const useDownloadCustomerCardPdf = () => {
    return useMutation({
        mutationFn: downloadCustomerCardPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося відкрити звіт"));
        },
    });
};

export const useCreateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Картка клієнта успішно створена")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити картку клієнта"))
        }
    })
}

export const useUpdateCustomerCard = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateCustomerCard,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [QUERY_KEY]})
            toast.success("Картка клієнта успішно оновлена")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося оновити картку клієнта"))
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
    options?: { enabled?: boolean }
) => {
    const effectiveSurname = (sorted || percent !== undefined) ? cardSurname : undefined;
    return useQuery({
        queryKey: [QUERY_KEY, cardNumber, effectiveSurname, percent, search_surname, sorted],
        queryFn: () => listCustomerCard(cardNumber, effectiveSurname, percent, search_surname, sorted),
        placeholderData: (previousData) => previousData,
        enabled: options?.enabled ?? true,
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
            toast.error(getErrorMessage(error, "Не вдалося видалити картку клієнта"))
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

export const useCustomerCardHistory = (cardNumber: string, isEnabled: boolean) => {
    return useQuery({
        queryKey: ["customer-card-history", cardNumber],
        queryFn: () => getCustomerCardHistory(cardNumber),
        enabled: isEnabled,
    });
};