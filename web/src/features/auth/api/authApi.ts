import {
    type Login,
    type LoginResponse,
    LoginResponseSchema,
} from "@/features/auth/types/types.ts";
import {javaApiClient} from "@/lib/axios.ts";
import type {CreateEmployee} from "@/features/employee/types/types.ts";

const prefix = "/auth"

export const login = async (payload: Login): Promise<LoginResponse> => {
    const response = await javaApiClient.post(prefix + "/login", payload)
    return LoginResponseSchema.parse(response.data);
}

export const register  = async (payload: CreateEmployee): Promise<void> => {
    await javaApiClient.post(prefix + "/registration", payload);
};
