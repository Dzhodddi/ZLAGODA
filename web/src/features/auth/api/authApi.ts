import {
    type Login,
    type LoginResponse,
    LoginResponseSchema,
    type RegisterEmployee
} from "@/features/auth/types/types.ts";
import {javaApiClient} from "@/lib/axios.ts";


const prefix = "/auth"

export const login = async (payload: Login): Promise<LoginResponse> => {
    const response = await javaApiClient.post(prefix + "/login", payload)
    return LoginResponseSchema.parse(response.data);
}

export const register = async (payload: RegisterEmployee): Promise<void> => {
    await javaApiClient.post(prefix + "/registration", payload);
};
