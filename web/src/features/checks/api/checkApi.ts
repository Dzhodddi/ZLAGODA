import {javaApiClient} from "@/lib/axios.ts";
import {z} from "zod";
import {type smallCheck, smallCheckSchema} from "@/features/checks/types/types.ts";

const prefix = "/checks"

export const getAllChecks
    = async (): Promise<smallCheck[]> => {
    const response = await javaApiClient.get(prefix);
    return z.array(smallCheckSchema).parse(response.data);
}
