import {UpsertCategoryForm} from "@/features/category/components/categoryForm.tsx";

export const CreateCategoryPage = () => {
    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Categories / New</h1>
            <UpsertCategoryForm />
        </div>
    );
};