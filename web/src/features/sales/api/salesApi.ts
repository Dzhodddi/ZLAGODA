import {goApiClient} from '@/lib/axios.ts';
import { z } from 'zod';
import {type Sale, SaleSchema} from "@/features/sales/types/types.ts";

const prefix = '/sales';

export const listSales = async (
    startDate: string,
    endDate: string,
    checkNumber: string | undefined = undefined,
    upc: string | undefined = undefined
): Promise<Sale[]> => {
    const params: Record<string, string> = {
        StartDate: startDate,
        EndDate: endDate,
    };

    if (checkNumber) {
        params.checkNumber = checkNumber;
    }
    if (upc) {
        params.upc = upc;
    }

    const response = await goApiClient.get(prefix, { params });

    if (!response.data) return [];
    return z.array(SaleSchema).parse(response.data);
};