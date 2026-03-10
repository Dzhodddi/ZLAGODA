import {javaApiClient} from "@/lib/axios.ts";
import {
    type Employee,
    EmployeeSchema,
    type CreateEmployee,
    PageEmployeeSchema,
    type EmployeeContact,
    PageEmployeeContactSchema
} from "@/features/employee/types/types.ts";

const prefix = "/employees"

export const createEmployee = async (data: CreateEmployee): Promise<Employee> => {
    const response = await javaApiClient.post(prefix, data);
    return EmployeeSchema.parse(response.data);
}

export const updateEmployee = async (idEmployee: string, data: CreateEmployee): Promise<Employee> => {
    const response = await javaApiClient.put(prefix  + "/" + idEmployee, data);
    return EmployeeSchema.parse(response.data);
}

export interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
}

export const getAllEmployees = async (): Promise<PageResponse<Employee>> => {
    const response = await javaApiClient.get(prefix);
    return PageEmployeeSchema.parse(response.data);
};

export const deleteEmployee = async (idEmployee: string): Promise<void> => {
    await javaApiClient.delete(prefix + "/" + idEmployee);
}

export const downloadEmployeePdf = async (): Promise<Blob> => {
    const response = await javaApiClient.get(prefix  + "/report", {
        responseType: "blob",
    });
    return response.data;
};

export const getAllCashiers = async (): Promise<PageResponse<Employee>> => {
    const response = await javaApiClient.get(prefix  + "/cashiers");
    return PageEmployeeSchema.parse(response.data);
};

export const getMe = async (): Promise<Employee> => {
    const response = await javaApiClient.get(prefix  + "/me");
    return EmployeeSchema.parse(response.data);
};

export const getEmployeePhoneAndAddress  = async (surname: string): Promise<PageResponse<EmployeeContact>> => {
    const response = await javaApiClient.get(prefix, {
        params: { surname },
    });
    return PageEmployeeContactSchema.parse(response.data);
}
