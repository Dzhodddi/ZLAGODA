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
import {getErrorMessage} from "@/lib/errorUtils.ts";

const QUERY_KEY = "employees";

export const useCreateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: createEmployee,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["employees-list"] });
            queryClient.invalidateQueries({ queryKey: ["cashiers-list"] });
            toast.success("Працівник успішно створений");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося створити працівника"));
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
            toast.success("Працівник успішно оновлений")
        },
        onError: (error) => {
            toast.error(getErrorMessage(error, "Не вдалося оновити працівника"))
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
            toast.error(getErrorMessage(error, "Не вдалося видалити працівника"))
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

export const useEmployeePhoneAndAddress = (surname: string | null, page: number, enabled = true) =>
    useQuery({
        queryKey: ["employees-contact", surname, page],
        queryFn: () => getEmployeePhoneAndAddress(surname, page),
        enabled: enabled,
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
            const newTab = window.open("", "_blank");
            if (newTab) {
                newTab.document.write(`
                    <!DOCTYPE html>
                    <html>
                    <head><title>employees.pdf</title></head>
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


export const useEmployeeIDList = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "id-list"],
        queryFn: () => getEmployeeIDList(),
        staleTime: staleTime,
    })
}
