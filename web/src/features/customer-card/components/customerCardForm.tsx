import { useRef } from "react";
import { Form } from "@/components/ui/Form.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";
import {useCreateCustomerCard} from "@/features/customer-card/hooks/useCustomerCard.ts";
import {
    type CreateCustomerCard,
    CreateCustomerCardSchema,
    type CustomerCard
} from "@/features/customer-card/types/types.ts";

export const UpsertCustomerCardForm = () => {
    const resetFormRef = useRef<() => void>(null);

    const mutation = useCreateCustomerCard();

    const handleSubmit = (data: CreateCustomerCard) => {
        console.log(data)
        mutation.mutate(data, {
            onSuccess: () => {
                if (resetFormRef.current) {
                    resetFormRef.current();
                }
            }
        });
    };

    return (
        <div className="p-4 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Create New Customer Card</h2>

            <Form<CustomerCard>
                schema={CreateCustomerCardSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting }, reset }) => {
                    resetFormRef.current = reset
                    return (
                        <>
                            <InputField name="cardNumber" label="Card number" />
                            <InputField name="customerName" label="Customer name" />
                            <InputField name="customerSurname" label="Customer surname" />
                            <InputField name="customerPatronymic" label="Customer patronymic" />
                            <InputField name="phoneNumber" label="Phone number" />
                            <InputField name="city" label="City" />
                            <InputField name="street" label="Street"/>
                            <InputField name="zipCode" label="Zipcode" />
                            <InputField name="customerPercent" label="Customer percent" />

                            <div className="col-span-12 flex justify-end mt-4">
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSubmitting ? 'Saving...' : 'Create customer card'}
                                </button>
                            </div>
                        </>
                    )
                }}
            </Form>
        </div>
    );
};