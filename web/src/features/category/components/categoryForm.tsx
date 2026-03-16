import { useCreateCategory, useUpdateCategory } from "@/features/category/hooks/useCategory.ts";
import { type Category, CreateCategorySchema } from "@/features/category/types/types.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";
import {useNavigate} from "react-router-dom";

interface Props {
    initialData?: Category;
}

export const UpsertCategoryForm = ({ initialData }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">

            <GenericUpsertForm
                schema={CreateCategorySchema}
                initialData={initialData}
                createMutation={useCreateCategory()}
                updateMutation={useUpdateCategory()}
                onSuccessAction={() => navigate("/categories")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    categoryNumber: initial.categoryNumber
                })}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving, isDirty }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            {isEditMode ? "Редагувати категорію" : "Додати категорію"}
                        </h2>

                        <InputField name="categoryName" label="Назва категорії" />

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
                )}
            </GenericUpsertForm>

        </div>
    );
};