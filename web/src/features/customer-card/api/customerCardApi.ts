import {goApiClient} from "@/lib/axios.ts";
import {type CreateCustomerCard, type CustomerCard, CustomerCardSchema} from "@/features/customer-card/types/types.ts";
import {z} from "zod";

const prefix = "/customer-cards"

export const createCustomerCard = async (data: CreateCustomerCard): Promise<CustomerCard> => {
    const response = await goApiClient.post(prefix, data);
    return CustomerCardSchema.parse(response.data);
}

export const updateCustomerCard = async (data: CreateCustomerCard): Promise<CustomerCard> => {
    const response = await goApiClient.put(`${prefix}/${data.cardNumber}`, data);
    return CustomerCardSchema.parse(response.data);
}

export const getCustomerCard = async (customerCardNumber: string): Promise<CustomerCard> => {
    const response = await goApiClient.get(prefix + "/" + customerCardNumber);
    return CustomerCardSchema.parse(response.data);
}

export const deleteCustomerCard = async (customerCardNumber: string): Promise<void> => {
    await goApiClient.delete(prefix + "/" + customerCardNumber);
}

export const listCustomerCard = async (
    card_number: string | undefined = undefined,
    surname: string | undefined = undefined,
    percent: number | undefined = undefined,
    sorted: boolean | undefined = undefined
): Promise<CustomerCard[]> => {
    const response = await goApiClient.get(prefix, {
        params: {
            card_number,
            percent,
            surname: sorted ? surname : undefined,
            sorted
        }
    });
    if (!response.data)
        return []
    return z.array(CustomerCardSchema).parse(response.data);
}
