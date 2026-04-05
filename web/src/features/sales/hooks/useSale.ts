import { useQuery} from "@tanstack/react-query";
import {listSales} from "@/features/sales/api/salesApi.ts";

export const useSaleList = (
    startDate: string,
    endDate: string,
    checkNumber: string | undefined = undefined,
    upc: string | undefined = undefined,
    options?: { enabled?: boolean }
) => {
    return useQuery({
        queryKey: ["sales", startDate, endDate, checkNumber, upc],
        queryFn: () => listSales(startDate, endDate, checkNumber, upc),
        placeholderData: (previousData) => previousData,
        enabled: options?.enabled ?? true,
    });
};
