import { InputField } from "@/components/ui/InputFields.tsx";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import {
    useCreateCustomerCard,
    useUpdateCustomerCard
} from "@/features/customer-card/hooks/useCustomerCard.ts";
import {
    type CreateCustomerCard,
    CreateCustomerCardSchema,
    type CustomerCard
} from "@/features/customer-card/types/types.ts";

interface Props {
    initialData?: CustomerCard;
}

export const UpsertCustomerCardForm = ({ initialData }: Props) => {
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-3xl mx-auto">

            <GenericUpsertForm<CreateCustomerCard, CreateCustomerCard>
                schema={CreateCustomerCardSchema}
                initialData={initialData}
                createMutation={useCreateCustomerCard()}
                updateMutation={useUpdateCustomerCard()}

                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    cardNumber: initial.cardNumber
                })}

                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4 border-b pb-2">
                            {isEditMode ? `Update Card: ${initialData?.cardNumber}` : 'Create New Customer Card'}
                        </h2>

                        <div className="col-span-12 sm:col-span-6">
                            <InputField
                                name="cardNumber"
                                label="Card number"
                                disabled={isEditMode}
                            />
                        </div>
                        <div className="col-span-12 sm:col-span-6">
                            <InputField
                                name="customerPercent"
                                label="Customer percent (%)"
                                type="number"
                            />
                        </div>

                        <div className="col-span-12 sm:col-span-4">
                            <InputField name="customerName" label="Customer name" />
                        </div>
                        <div className="col-span-12 sm:col-span-4">
                            <InputField name="customerSurname" label="Customer surname" />
                        </div>
                        <div className="col-span-12 sm:col-span-4">
                            <InputField name="customerPatronymic" label="Patronymic (Optional)" />
                        </div>

                        <div className="col-span-12 sm:col-span-6">
                            <InputField name="phoneNumber" label="Phone number" />
                        </div>
                        <div className="col-span-12 sm:col-span-6">
                            <InputField name="city" label="City (Optional)" />
                        </div>

                        <div className="col-span-12 sm:col-span-6">
                            <InputField name="street" label="Street (Optional)" />
                        </div>
                        <div className="col-span-12 sm:col-span-6">
                            <InputField name="zipCode" label="Zipcode (Optional)" />
                        </div>

                        <div className="col-span-12 flex justify-end mt-4 pt-4 border-t">
                            <button
                                type="submit"
                                disabled={isSaving}
                                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
                            >
                                {isSaving ? 'Saving...' : isEditMode ? 'Update Card' : 'Create Card'}
                            </button>
                        </div>
                    </>
                )}
            </GenericUpsertForm>
        </div>
    );
};