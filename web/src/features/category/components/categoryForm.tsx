import { useRef } from "react"; // 1. Import useRef
import { useCreateCategory } from "@/features/category/hooks/useCategory.ts";
import { type Category, type CreateCategory, CreateCategorySchema } from "@/features/category/types/types.ts";
import { Form } from "@/components/ui/Form.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";

export const CreateCategoryForm = () => {
    const resetFormRef = useRef<() => void>(null);

    const mutation = useCreateCategory();

    const handleSubmit = (data: CreateCategory) => {
        mutation.mutate(data, {
            onSuccess: () => {
                if (resetFormRef.current) {
                    resetFormRef.current();
                }
            }
        });
    };

    return (
        <div className="p-6 bg-white rounded shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Create New User</h2>

            <Form<Category>
                schema={CreateCategorySchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting }, reset }) => {
                    resetFormRef.current = reset
                    return (
                        <>
                            <InputField name="categoryName" label="Category Name" />

                            <div className="col-span-12 flex justify-end mt-4">
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSubmitting ? 'Saving...' : 'Create category'}
                                </button>
                            </div>
                        </>
                    )
                }}
            </Form>
        </div>
    );
};