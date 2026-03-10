import { useParams, useNavigate } from "react-router-dom";
import { useCreateStoreProduct, useUpdateStoreProduct } from "@/features/store_product/hooks/useStoreProduct.ts";
import { type CreateStoreProduct, CreateStoreProductSchema } from "@/features/store_product/types/types.ts";
import { Form } from "@/components/ui/Form.tsx";
import { InputField, CheckboxField } from "@/components/ui/InputFields.tsx";

export const UpsertStoreProductForm = () => {
    const { upc } = useParams();
    const navigate = useNavigate();
    const isEditing = !!upc;

    const createMutation = useCreateStoreProduct();
    const updateMutation = useUpdateStoreProduct();

    const handleSubmit = (formData: CreateStoreProduct) => {
        if (isEditing) {
            updateMutation.mutate(
                { upc: upc!, data: formData },
                { onSuccess: () => navigate("/store-product") }
            );
        } else {
            createMutation.mutate(formData, {
                onSuccess: () => navigate("/store-product")
            });
        }
    };

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold">
                    {isEditing ? `Редагувати: ${upc}` : "Створити товар у магазині"}
                </h2>
                <button
                    onClick={() => navigate("/store-product")}
                    className="text-gray-500 hover:text-gray-700 text-sm"
                >
                    ← Назад
                </button>
            </div>

            <Form<CreateStoreProduct>
                schema={CreateStoreProductSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting } }) => (
                    <>
                        <InputField name="upc" label="UPC" />
                        <InputField name="upcProm" label="UPC промо" />
                        <InputField name="idProduct" label="ID товару" />
                        <InputField type="number" name="price" label="Ціна продажу" />
                        <InputField type="number" name="productsNumber" label="Кількість" />
                        <CheckboxField name="promotionalProduct" label="Акційний товар" />

                        <div className="col-span-12 flex justify-end gap-2 mt-4">
                            <button
                                type="button"
                                onClick={() => navigate("/store-product")}
                                className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
                            >
                                Скасувати
                            </button>
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                            >
                                {isSubmitting ? "Збереження..." : isEditing ? "Оновити" : "Створити"}
                            </button>
                        </div>
                    </>
                )}
            </Form>
        </div>
    );
};
