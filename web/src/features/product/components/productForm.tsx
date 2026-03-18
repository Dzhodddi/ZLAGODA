import { InputField } from "@/components/ui/InputFields.tsx";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { useCreateProduct, useUpdateProduct } from "@/features/product/hooks/useProduct.ts";
import {
    type CreateProduct,
    CreateProductSchema,
    type Product,
    BaseProductSchema,
} from "@/features/product/types/types.ts";
import { useNavigate } from "react-router-dom";
import {useAllCategories} from "@/features/category/hooks/useCategory.ts";
import {SelectField, type SelectFieldProps} from "@/components/ui/SelectField.tsx";

interface Props {
    initialData?: Product;
}

export const CategorySelect = (props: Omit<SelectFieldProps, 'name' | 'label' | 'options' | 'valueAsNumber'>) => {
    const { data: categories } = useAllCategories();
    const options = categories?.map(c => ({ value: c.categoryNumber, label: c.categoryName })) ?? [];

    return <SelectField name="categoryNumber" label="Категорія" options={options} valueAsNumber {...props} />;
};

export const UpsertProductForm = ({ initialData }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <GenericUpsertForm<CreateProduct, CreateProduct & { id: number }, Product>
                schema={initialData ? BaseProductSchema : CreateProductSchema}
                initialData={initialData}
                createMutation={useCreateProduct()}
                updateMutation={useUpdateProduct()}
                onSuccessAction={() => navigate("/product")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    id: initial.idProduct,
                } as CreateProduct & { id: number })}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving, isDirty }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            {isEditMode ? "Редагувати товар" : "Додати товар"}
                        </h2>
                        <InputField name="productName" label="Назва товару" />
                        <CategorySelect required />
                        <InputField name="producer" label="Виробник" />
                        <InputField name="productCharacteristics" label="Характеристики" />

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
