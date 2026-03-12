import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createEmployee,
    updateEmployee,
    getAllEmployees,
    deleteEmployee,
    getAllCashiers,
    getMe,
    getEmployeePhoneAndAddress,
    downloadEmployeePdf, getEmployee,
} from "@/features/employee/api/employeeApi.ts";
import type {CreateEmployee} from "@/features/employee/types/types.ts";

const QUERY_KEY = "employees";

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            console.error(error.message);
        },
    });
};

export const useUpdateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: CreateEmployee) => updateEmployee(data.idEmployee, data), // ✅
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            console.error(error.message);
        },
    });
};

export const useDeleteEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
        },
        onError: (error) => {
            console.error(error.message);
        }
    });
};

export const useAllEmployees = () => {
    return useQuery({
        queryKey: [QUERY_KEY],
        queryFn: getAllEmployees,
        staleTime: 1000 * 30,
    });
};

export const useAllCashiers = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "cashiers"],
        queryFn: getAllCashiers,
        staleTime: 1000 * 30,
    });
};

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
        staleTime: 1000 * 30,
    });
};

export const useEmployeePhoneAndAddress = (surname: string) => {
    return useQuery({
        queryKey: [QUERY_KEY, "contact", surname],
        queryFn: () => getEmployeePhoneAndAddress(surname),
        enabled: !!surname,
        staleTime: 1000 * 30,
    });
};

export const useDownloadEmployeePdf = () => {
    return useMutation({
        mutationFn: downloadEmployeePdf,
        onSuccess: (blob) => {
            const url = URL.createObjectURL(blob);
            const win = window.open(url);
            win?.print();
            URL.revokeObjectURL(url);
        },
        onError: (error) => {
            console.error(error.message);
        },
    });
};
