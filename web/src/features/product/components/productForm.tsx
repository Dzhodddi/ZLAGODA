import { useParams, useNavigate } from "react-router-dom";
import { useCreateProduct, useUpdateProduct } from "@/features/product/hooks/useProduct";
import { type CreateProduct, CreateProductSchema } from "@/features/product/types/types";
import { Form } from "@/components/ui/Form";
import { InputField } from "@/components/ui/InputFields";

export const UpsertProductForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditing = !!id;

    const createMutation = useCreateProduct();
    const updateMutation = useUpdateProduct();

    const handleSubmit = (data: CreateProduct) => {
        if (isEditing) {
            updateMutation.mutate(
                { id: Number(id), data },
                { onSuccess: () => navigate("/product") }
            );
        } else {
            createMutation.mutate(data, {
                onSuccess: () => navigate("/product"),
            });
        }
    };

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold">
                    {isEditing ? `Редагувати товар #${id}` : "Створити товар"}
                </h2>
                <button
                    onClick={() => navigate("/product")}
                    className="text-gray-500 hover:text-gray-700 text-sm"
                >
                    ← Назад
                </button>
            </div>

            <Form<CreateProduct>
                schema={CreateProductSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting } }) => (
                    <>
                        <InputField type="number" name="categoryNumber"       label="Номер категорії" />
                        <InputField              name="productName"            label="Назва товару" />
                        <InputField              name="productCharacteristics" label="Характеристики" />

                        <div className="col-span-12 flex justify-end gap-2 mt-4">
                            <button
                                type="button"
                                onClick={() => navigate("/product")}
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
