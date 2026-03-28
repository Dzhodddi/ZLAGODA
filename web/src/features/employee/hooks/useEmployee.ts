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
import {staleTime} from "@/constants/constants.ts";

const QUERY_KEY = "employees";

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["employees-list"] });
            queryClient.invalidateQueries({ queryKey: ["cashiers-list"] });
        },
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
        },
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
            console.error(error.message);
        },
    });
};
