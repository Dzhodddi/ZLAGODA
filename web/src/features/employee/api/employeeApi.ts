import {javaApiClient} from "@/lib/axios.ts";
import {
    type Employee,
    type CreateEmployee,
    PageEmployeeSchema,
    PageEmployeeContactSchema,
    BaseEmployeeSchema
} from "@/features/employee/types/types.ts";

const prefix = "/employees"

export const getEmployee = async (idEmployee: string): Promise<Employee> => {
    const response = await javaApiClient.get(prefix  + "/" + idEmployee);
    return BaseEmployeeSchema.parse(response.data);
}

export const getAllEmployees = async (page = 0, sortedBySurname = false) => {
    const response = await javaApiClient.get(prefix, {
        params: { page, ...(sortedBySurname && { sortedBySurname: true }) },
    });
    return PageEmployeeSchema.parse(response.data);
};

export const getAllCashiers = async (page = 0, sortedBySurname = false) => {
    const response = await javaApiClient.get(prefix + "/cashiers", {
        params: { page, ...(sortedBySurname && { sortedBySurname: true }) },
    });
    return PageEmployeeSchema.parse(response.data);
};

export const getEmployeePhoneAndAddress = async (surname: string, page = 0) => {
    const response = await javaApiClient.get(prefix, { params: { surname, page } });
    return PageEmployeeContactSchema.parse(response.data);
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

export const createEmployee = async (data: CreateEmployee): Promise<Employee> => {
    const response = await javaApiClient.post(prefix, data);
    return BaseEmployeeSchema.parse(response.data);
};

export const updateEmployee = async (data: Employee): Promise<Employee> => {
    const response = await javaApiClient.put(prefix + "/" + data.idEmployee, data);
    return BaseEmployeeSchema.parse(response.data);
};

export const getMe = async (): Promise<Employee> => {
    const response = await javaApiClient.get(prefix + "/me");
    return BaseEmployeeSchema.parse(response.data);
};
