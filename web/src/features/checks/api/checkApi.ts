import { goApiClient } from '@/lib/axios.ts';
import { z } from 'zod';
import {
    type Check,
    CheckSchema,
    CheckItemSchema, type CheckItem
} from "@/features/checks/types/types.ts";

const prefix = '/checks';

export const createCheck = async (data: Check): Promise<Check> => {
    const response = await goApiClient.post(prefix, data);
    return CheckSchema.parse(response.data);
};

export const updateCheck = async (data: Check): Promise<Check> => {
    const response = await goApiClient.put(`${prefix}/${data}`, data);
    return CheckSchema.parse(response.data);
};

export const getCheck = async (checkNumber: string): Promise<CheckItem> => {
    const response = await goApiClient.get(prefix + '/' + checkNumber);
    return CheckItemSchema.parse(response.data);
};

export const deleteCheck = async (checkNumber: string): Promise<void> => {
    await goApiClient.delete(`${prefix}/${checkNumber}`);
};

export const listChecks = async (
    startDate: string,
    endDate: string,
    employeeId?: string
): Promise<CheckItem[]> => {
    const params: Record<string, string> = {
        StartDate: startDate,
        EndDate: endDate,
    };

    if (employeeId) {
        params.employeeId = employeeId;
    }

    const response = await goApiClient.get(prefix, { params });

    if (!response.data) return [];
    return z.array(CheckItemSchema).parse(response.data);
};


export const getChecksTotalSum = async (
    startDate: string,
    endDate: string,
    employeeId?: string
): Promise<number> => {
    const params: Record<string, string> = {
        start_date: startDate,
        end_date: endDate
    };
    if (employeeId) params.employee_id = employeeId;

    const response = await goApiClient.get(`${prefix}/price`, { params });
    return Number(response.data?.totalPrice || 0);
};