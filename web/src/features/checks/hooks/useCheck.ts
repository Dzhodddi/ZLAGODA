import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import {staleTime} from "@/constants/constants.ts";
import {
    createCheck,
    deleteCheck, downloadCheckPdf,
    getCheck,
    getChecksTotalSum,
    getTodayChecks,
    listChecks,
} from "@/features/checks/api/checkApi.ts";
import {isAxiosError} from "axios";

export const useDownloadCheckPdf = () => {
    return useMutation({
        mutationFn: downloadCheckPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => alert(error),
    });
};

export const useCreateCheck = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCheck,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["checks"] });
            toast.success("Чек успішно створено");
        },
        onError: (error) => {
            toast.error("Помилка під час створення чеку");
            console.error(error);
        }
    });
};

export const useDeleteCheck = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCheck,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["checks"] });
        },
        onError: (error) => {
            if (error instanceof Error && error.message.includes("400")) {
                toast.error("Чек використовується!");
                return;
            }
            toast.error("Помилка під час видалення чеку");
            console.error(error);
        }
    });
};

export const useCheckList = (
    startDate: string,
    endDate: string,
    employeeId?: string,
    checkNumber?: string,
    enabled: boolean = true
) => {
    return useQuery({
        queryKey: ["checks", startDate, endDate, employeeId, checkNumber],
        queryFn: async () => {
            try {
                return await listChecks(startDate, endDate, employeeId, checkNumber);
            } catch (error) {
                if (employeeId && isAxiosError(error) && error.response?.status === 400) {
                    toast.error(`Касира з таким ${employeeId!} не знайдено або невірний формат`);
                }
                return []
            }
        },
        placeholderData: (previousData) => previousData,
        enabled: enabled,
    });
};

export const useCheck = (checkNumber: string) => {
    return useQuery({
        queryKey: ["checks", checkNumber],
        queryFn: () => getCheck(checkNumber),
        enabled: !!checkNumber,
        staleTime: staleTime,
    });
};

export const useCheckTotalSum = (
    startDate: string,
    endDate: string,
    employeeId?: string,
    enabled: boolean = true
) => {
    return useQuery({
        queryKey: ["checks-total-sum", startDate, endDate, employeeId],
        queryFn: async () => {
            try {
                return await getChecksTotalSum(startDate, endDate, employeeId);
            } catch (error) {
                if (employeeId && isAxiosError(error) && error.response?.status === 400) {
                    toast.error(`Касира з таким ${employeeId!} не знайдено або невірний формат`);
                }
                return 0
            }
        },
        enabled: enabled && Boolean(startDate && endDate && new Date(startDate) <= new Date(endDate)),
    });
};

export const useTodayCheckList = (
    employeeId: string,
    enabled: boolean,
    checkNumber?: string
) => {
    return useQuery({
        queryKey: ["checks-today", employeeId, checkNumber],
        queryFn: async () => {
            try {
                return await getTodayChecks(employeeId, checkNumber);
            } catch (error) {
                if (employeeId && isAxiosError(error) && error.response?.status === 400) {
                    toast.error(`Касира з таким ${employeeId!} не знайдено або невірний формат`);
                }
                return []
            }
        },
        enabled: enabled && Boolean(employeeId),
        placeholderData: (previousData) => previousData,
    });
};