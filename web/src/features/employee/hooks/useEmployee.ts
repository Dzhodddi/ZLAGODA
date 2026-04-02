import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createEmployee,
    updateEmployee,
    getAllEmployees,
    deleteEmployee,
    getAllCashiers,
    getMe,
    getEmployeePhoneAndAddress,
    downloadEmployeePdf, getEmployee, getEmployeeIDList,
} from "@/features/employee/api/employeeApi.ts";
import {staleTime} from "@/constants/constants.ts";
import {toast} from "sonner";
import {isAxiosError} from "axios";

const QUERY_KEY = "employees";

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["employees-list"] });
            queryClient.invalidateQueries({ queryKey: ["cashiers-list"] });
            toast.success("Успішно створено працівника");
        },
        onError: (error) => {
            if (isAxiosError(error) && error.response?.data) {
                if (error.response.status === 409) {
                    toast.error("Працівник з таким ID уже існує");
                    return;
                }
            }
            toast.error("Не вдалося створити працівника");
            console.error(error);
        }
    });
};

export const useUpdateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: updateEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["employees-list"] });
            queryClient.invalidateQueries({ queryKey: ["cashiers-list"] });
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            toast.success("Успішно оновлено працівника")
        },
        onError: (error) => {
            toast.error("Не вдалося оновити працівника")
            console.error(error);
        }
    });
};

export const useDeleteEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["employees-list"] });
            queryClient.invalidateQueries({ queryKey: ["cashiers-list"] });
        },
        onError: (error) => {
            if (isAxiosError(error) && error.response?.data) {
                toast.error("Не вдалося видалити працівника")
                console.error(error);
                return;
            }
            toast.error("Помилка підключення до сервера");
            console.error(error);
        }
    });
};

export const useAllEmployees = (page: number, enabled = true, sortedBySurname: boolean) =>
    useQuery({
        queryKey: ["employees-list", page, sortedBySurname],
        queryFn: () => getAllEmployees(page, sortedBySurname),
        enabled,
        staleTime: staleTime,
    });

export const useAllCashiers = (page: number, enabled = true, sortedBySurname: boolean) =>
    useQuery({
        queryKey: ["cashiers-list", page, sortedBySurname],
        queryFn: () => getAllCashiers(page, sortedBySurname),
        enabled,
        staleTime: staleTime,
    });

export const useEmployeePhoneAndAddress = (surname: string, page: number, enabled = true) =>
    useQuery({
        queryKey: ["employees-contact", surname, page],
        queryFn: () => getEmployeePhoneAndAddress(surname, page),
        enabled: !!surname && enabled,
        staleTime: staleTime,
    });

export const useGetMe = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "me"],
        queryFn: getMe,
        staleTime: 1000 * 60,
    });
};

export const useEmployee = (id: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, id],
        queryFn: () => getEmployee(id),
        enabled: !!id,
        staleTime: staleTime,
    });
};

export const useDownloadEmployeePdf = () => {
    return useMutation({
        mutationFn: downloadEmployeePdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            window.open(url);
        },
        onError: (error) => {
            toast.error("Не вдалося відкрити звіт");
            console.error(error.message);
        },
    });
};


export const useEmployeeIDList = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "id-list"],
        queryFn: () => getEmployeeIDList(),
        staleTime: staleTime,
    })
}
