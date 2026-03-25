import { useFieldArray } from "react-hook-form";
import { InputField } from "@/components/ui/InputFields.tsx";
import { type Check, CheckSchema } from "@/features/checks/types/types.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { useCreateCheck, useUpdateCheck } from "@/features/checks/hooks/useCheck.ts";
import { useNavigate } from "react-router-dom";

interface Props {
    initialData?: Check;
}

export const UpsertCheckForm = ({ initialData }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-4xl mx-auto">
            <GenericUpsertForm
                schema={CheckSchema}
                initialData={initialData}
                createMutation={useCreateCheck()}
                updateMutation={useUpdateCheck()}
                onSuccessAction={() => navigate("/checks")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    checkNumber: initial.checkNumber
                })}
                className="grid grid-cols-12 gap-4"
            >
                {({ control, formState: { errors } }, { isEditMode, isSaving, isDirty }) => {
                    const { fields, append, remove } = useFieldArray({
                        control,
                        name: "products",
                    });

                    return (
                        <>
                            <h2 className="col-span-12 text-xl font-bold mb-4">
                                {isEditMode ? "Редагувати чек" : "Створити новий чек"}
                            </h2>

                            <InputField
                                name="checkNumber"
                                label="Номер чеку"
                                disabled={isEditMode}
                            />
                            <InputField
                                name="idEmployee"
                                label="ID працівника"
                            />
                            <InputField
                                name="cardNumber"
                                label="Номер картки клієнта"
                            />
                            <InputField
                                name="printDate"
                                label="Дата друку"
                                type="datetime-local"
                            />
                            <InputField
                                name="vat"
                                label="Сума ПДВ"
                                type="number"
                                step="0.0001"
                            />

                            <div className="col-span-12 mt-4">
                                <h3 className="font-semibold mb-2">Товари</h3>

                                {fields.map((field, index) => (
                                    <div key={field.id} className="flex gap-4 items-end mb-3 bg-zinc-50 p-2 rounded border border-zinc-200">
                                        <div className="flex-1">
                                            <InputField
                                                name={`products.${index}.upc`}
                                                label="UPC"
                                            />
                                        </div>
                                        <div className="w-32">
                                            <InputField
                                                name={`products.${index}.quantity`}
                                                label="Кількість"
                                                type="number"
                                            />
                                        </div>
                                        <button
                                            type="button"
                                            onClick={() => remove(index)}
                                            className="bg-red-500 text-white px-3 py-2 rounded h-10.5 hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            Видалити
                                        </button>
                                    </div>
                                ))}

                                {errors.products?.message && (
                                    <p className="text-red-500 text-sm mt-1 mb-2 font-medium">
                                        {String(errors.products.message)}
                                    </p>
                                )}

                                <button
                                    type="button"
                                    onClick={() => append({ upc: "", quantity: 1 })}
                                    className="mt-2 text-blue-600 text-sm font-medium hover:underline"
                                >
                                    + Додати товар
                                </button>
                            </div>

                            <div className="col-span-12 flex justify-end mt-4">
                                <button
                                    type="submit"
                                    disabled={isSaving || (isEditMode && !isDirty)}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSaving ? 'Збереження...' : isEditMode ? 'Оновити' : 'Створити'}
                                </button>
                            </div>
                        </>
                    );
                }}
            </GenericUpsertForm>
        </div>
    );
};