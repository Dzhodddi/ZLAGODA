import {goApiClient} from "@/lib/axios.ts";
import {type CreateCustomerCard, type CustomerCard, CustomerCardSchema} from "@/features/customer-card/types/types.ts";

const prefix = "/customer-cards"

export const createCustomerCard = async (data: CreateCustomerCard): Promise<CustomerCard> => {
    const response = await goApiClient.post(prefix, data);
    return CustomerCardSchema.parse(response.data);
}

export const updateCustomerCard = async (data: CreateCustomerCard): Promise<CustomerCard> => {
    const response = await goApiClient.put(prefix, data);
    return CustomerCardSchema.parse(response.data);
}

export const getCustomerCard = async (customerCardNumber: string): Promise<CustomerCard> => {
    const response = await goApiClient.get(prefix + "/" + customerCardNumber);
    return CustomerCardSchema.parse(response.data);
}

export const deleteCustomerCard = async (customerCardNumber: string): Promise<void> => {
    await goApiClient.delete(prefix + "/" + customerCardNumber);
}
