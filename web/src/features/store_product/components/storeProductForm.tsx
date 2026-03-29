import { useNavigate } from "react-router-dom";
import {
    useCreateStoreProduct,
    useUpdateStoreProduct,
} from "@/features/store_product/hooks/useStoreProduct.ts";
import {
    type StoreProduct,
    type CreateStoreProduct,
    CreateStoreProductSchema,
    BaseStoreProductSchema,
} from "@/features/store_product/types/types.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { InputField, CheckboxField } from "@/components/ui/InputFields.tsx";
import { useWatch, useFormContext } from "react-hook-form";
import {ProductComboboxField} from "@/features/product/components/ProductComboBox.tsx";

interface Props {
    initialData?: StoreProduct;
}

const StoreProductFormFields = ({ isEditMode }: { isEditMode: boolean }) => {
    const { control } = useFormContext<CreateStoreProduct>();
    const isPromotional = useWatch({ control, name: "promotionalProduct" });

    return (
        <>
            <div className="col-span-12"><InputField name="upc" label="UPC" disabled={isEditMode} /></div>
            <div className="col-span-12"><ProductComboboxField /></div>
            <div className="col-span-12"><InputField type="number" name="sellingPrice" label="Ціна продажу" min="0" step="0.01" /></div>
            <div className="col-span-12"><InputField type="number" name="productsNumber" label="Кількість одиниць" min="0" /></div>
            <div className="col-span-12 my-2">
                <CheckboxField name="promotionalProduct" label="Акційний товар" />
            </div>
            {isPromotional && (
                <div className="col-span-12"><InputField name="upcProm" label="UPC промо" /></div>
            )}
        </>
    );
};

export const UpsertStoreProductForm = ({ initialData }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <GenericUpsertForm<CreateStoreProduct, CreateStoreProduct, StoreProduct>
                schema={initialData ? BaseStoreProductSchema : CreateStoreProductSchema}
                initialData={initialData}
                createMutation={useCreateStoreProduct()}
                updateMutation={useUpdateStoreProduct()}
                onSuccessAction={() => navigate("/store-product")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    upc: initial.upc,
                })}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving, isDirty }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            {isEditMode ? `Редагувати товар у магазині` : "Додати товар у магазині"}
                        </h2>

                        <StoreProductFormFields isEditMode={isEditMode} />

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
