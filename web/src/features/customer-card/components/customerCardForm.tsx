import { InputField } from "@/components/ui/InputFields.tsx";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import {
    useCreateCustomerCard,
    useUpdateCustomerCard
} from "@/features/customer-card/hooks/useCustomerCard.ts";
import {
    type CreateCustomerCard,
    CreateCustomerCardSchema,
    type CustomerCard, CustomerCardSchema
} from "@/features/customer-card/types/types.ts";
import {useNavigate} from "react-router-dom";

interface Props {
    initialData?: CustomerCard;
}

export const UpsertCustomerCardForm = ({ initialData }: Props) => {
    const navigate = useNavigate()
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-3xl mx-auto">

            <GenericUpsertForm<CreateCustomerCard, CreateCustomerCard, CustomerCard>
                schema={initialData ? CustomerCardSchema : CreateCustomerCardSchema}
                initialData={initialData}
                createMutation={useCreateCustomerCard()}
                updateMutation={useUpdateCustomerCard()}
                onSuccessAction={() => navigate("/customer-card")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    cardNumber: initial.cardNumber
                })}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving, isDirty }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            {isEditMode ? "Редагувати картку клієнта" : "Додати картку клієнта"}
                        </h2>
                        <InputField name="cardNumber" label="Номер карти" disabled={isEditMode} />
                        <InputField name="customerName" label="Ім'я" />
                        <InputField name="customerSurname" label="Прізвище" />
                        <InputField name="customerPatronymic" label="По батькові" required={false} />
                        <InputField name="customerPercent" label="Відсоток" min="1" max="100"/>
                        <InputField name="phoneNumber" label="Контактний телефон" />
                        <InputField name="city" label="Місто" required={false}/>
                        <InputField name="street" label="Вулиця" required={false}/>
                        <InputField name="zipCode" label="Поштовий індекс" required={false}/>

                        <div className="col-span-12 flex justify-end mt-4">
                            <button
                                type="submit"
                                disabled={isSaving || (isEditMode && !isDirty)}
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                            >
                                {isSaving ? "Збереження..." : isEditMode ? "Оновити" : "Створити"}
                            </button>
                        </div>
                    </>
                )}
            </GenericUpsertForm>
        </div>
    );
};