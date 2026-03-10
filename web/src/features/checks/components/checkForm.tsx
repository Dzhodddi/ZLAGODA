import { useRef } from "react";
import { useFieldArray } from "react-hook-form";
import { Form } from "@/components/ui/Form.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";
import {
    type CreateNewCheck,
    CreateNewCheckSchema
} from "@/features/checks/types/types.ts";

export const UpsertCheckForm = () => {
    const resetFormRef = useRef<() => void>(null);

    const handleSubmit = (data: CreateNewCheck) => {
        console.log("Submitting Check:", data);
    };

    return (
        <div className="p-4 bg-white rounded text-zinc-900 shadow-md max-w-4xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Create New Check</h2>

            <Form<CreateNewCheck>
                schema={CreateNewCheckSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
                defaultValues={{
                    products: [{ upc: "", quantity: 1 }],
                    print_date: new Date().toISOString(),
                }}
            >
                {({ control, formState: { isSubmitting }, reset }) => {
                    resetFormRef.current = reset;

                    const { fields, append, remove } = useFieldArray({
                        control,
                        name: "products",
                    });

                    return (
                        <>
                            <InputField name="check_number" label="Check Number" />
                            <InputField name="id_employee" label="Employee ID" />
                            <InputField name="card_number" label="Card Number (Optional)" />
                            <InputField name="print_date" label="Print Date" type="datetime-local" />
                            <InputField name="vat" label="VAT Amount" type="number" step="0.0001" />

                            <div className="col-span-12">
                                <h3 className="font-semibold mb-2">Products</h3>
                                {fields.map((field, index) => (
                                    <div key={field.id} className="flex gap-4 items-end mb-3 bg-zinc-50 p-2 rounded">
                                        <div className="flex-1">
                                            <InputField
                                                name={`products.${index}.upc`}
                                                label="UPC"
                                            />
                                        </div>
                                        <div className="w-32">
                                            <InputField
                                                name={`products.${index}.quantity`}
                                                label="Qty"
                                                type="number"
                                            />
                                        </div>
                                        <button
                                            type="button"
                                            onClick={() => remove(index)}
                                            className="bg-red-500 text-white px-3 py-2 rounded h-[42px] hover:bg-red-600"
                                            disabled={fields.length === 1}
                                        >
                                            Remove
                                        </button>
                                    </div>
                                ))}

                                <button
                                    type="button"
                                    onClick={() => append({ upc: "", quantity: 1 })}
                                    className="mt-2 text-blue-600 text-sm font-medium hover:underline"
                                >
                                    + Add another product
                                </button>
                            </div>

                            <div className="col-span-12 flex justify-end mt-6">
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSubmitting ? 'Saving...' : 'Print Check'}
                                </button>
                            </div>
                        </>
                    );
                }}
            </Form>
        </div>
    );
};