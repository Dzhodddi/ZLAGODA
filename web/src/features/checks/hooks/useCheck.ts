import {useQuery} from "@tanstack/react-query";
import {getAllChecks} from "@/features/checks/api/checkApi.ts";

const QUERY_KEY = "checks"

export const useAllChecks = () => {
    return useQuery({
        queryKey: [QUERY_KEY, "all"],
        queryFn: () => getAllChecks(),
        staleTime: 1000 * 30,
    });
};