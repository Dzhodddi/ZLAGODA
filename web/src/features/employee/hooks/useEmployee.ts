import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    createEmployee,
    updateEmployee,
    getAllEmployees,
    deleteEmployee,
    getAllCashiers,
    getMe,
    getEmployeePhoneAndAddress,
    downloadEmployeePdf,
} from "@/features/employee/api/employeeApi.ts";

const QUERY_KEY = "employees";

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully created employee!");
        },
        onError: (error) => alert(error),
    });
};

export const useUpdateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, data }: { id: string; data: Parameters<typeof updateEmployee>[1] }) =>
            updateEmployee(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully updated employee!");
        },
        onError: (error) => alert(error),
    });
};

export const useDeleteEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: deleteEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [QUERY_KEY] });
            alert("Successfully deleted employee!");
        },
        onError: (error) => alert(error),
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
            const a = document.createElement("a");
            a.href = url;
            a.download = "employees.pdf";
            a.click();
            URL.revokeObjectURL(url);
        },
        onError: (error) => alert(error),
    });
};
