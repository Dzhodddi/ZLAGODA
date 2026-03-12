import {javaApiClient} from "@/lib/axios.ts";
import {
    type Employee,
    EmployeeSchema,
    type CreateEmployee,
    PageEmployeeSchema,
    type EmployeeContact,
    PageEmployeeContactSchema,
    type PageResponse,
    type PageResponseContact
} from "@/features/employee/types/types.ts";

const prefix = "/employees"

export const getEmployee = async (idEmployee: string): Promise<Employee> => {
    const response = await javaApiClient.get(prefix  + "/" + idEmployee);
    return EmployeeSchema.parse(response.data);
}

export const createEmployee = async (data: CreateEmployee): Promise<Employee> => {
    const response = await javaApiClient.post(prefix, data);
    return EmployeeSchema.parse(response.data);
}

export const updateEmployee = async (idEmployee: string, data: CreateEmployee): Promise<Employee> => {
    const response = await javaApiClient.put(prefix  + "/" + idEmployee, data);
    return EmployeeSchema.parse(response.data);
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

export const getEmployeePhoneAndAddress  = async (surname: string): Promise<PageResponseContact<EmployeeContact>> => {
    const response = await javaApiClient.get(prefix, {
        params: { surname },
    });
    console.log(response.data);
    return PageEmployeeContactSchema.parse(response.data);
}
