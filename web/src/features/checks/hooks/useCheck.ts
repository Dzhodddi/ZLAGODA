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
import {getErrorMessage} from "@/lib/errorUtils.ts";

export const useDownloadCheckPdf = () => {
    return useMutation({
        mutationFn: downloadCheckPdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const newTab = window.open("", "_blank");
            if (newTab) {
                newTab.document.write(`
                    <!DOCTYPE html>
                    <html>
                    <head><title>checks.pdf</title></head>
                    <body style="margin:0">
                        <iframe 
                            src="${url}" 
                            style="width:100vw; height:100vh; border:none"
                            onload="this.contentWindow.print()"
                        ></iframe>
                    </body>
                    </html>
                `);
                newTab.document.close();
            }
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося відкрити звіт"));
        },
    });
};

export const useCreateCheck = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createCheck,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["checks"] });
            queryClient.invalidateQueries({ queryKey: ["checks-today"] });
            queryClient.invalidateQueries({ queryKey: ["checks-total-sum"] });
            toast.success("Чек успішно створений");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити чек"));
        }
    });
};

export const useDeleteCheck = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteCheck,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["checks"] });
            queryClient.invalidateQueries({ queryKey: ["checks-today"] });
            queryClient.invalidateQueries({ queryKey: ["checks-total-sum"] });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося видалити чек"));
        }
    });
};

export const useCheckList = (
    startDate: string,
    endDate: string,
    employeeId?: string,
    checkNumber?: string,
    options?: { enabled?: boolean }
) => {
    return useQuery({
        queryKey: ["checks", startDate, endDate, employeeId, checkNumber],
        queryFn: async () => {
            try {
                return await listChecks(startDate, endDate, employeeId, checkNumber);
            } catch (error) {
                if (employeeId && isAxiosError(error) && error.response?.status === 400) {
                    toast.error(`Касира з ID ${employeeId!} не знайдено, або неправильний формат`);
                }
                return []
            }
        },
        placeholderData: (previousData) => previousData,
        enabled: options?.enabled ?? true,
        staleTime: staleTime,
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
                    toast.error(`Касира з ID ${employeeId!} не знайдено, або неправильний формат`);
                }
                return 0
            }
        },
        enabled: enabled && Boolean(startDate && endDate && new Date(startDate) <= new Date(endDate)),
        staleTime: staleTime,
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
                    toast.error(`Касира з ID ${employeeId!} не знайдено, або неправильний формат`);
                }
                return []
            }
        },
        enabled: enabled && Boolean(employeeId),
        placeholderData: (previousData) => previousData,
    });
};