import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { getCategory } from "../api/categoryApi";
import { UpsertCategoryForm } from "@/features/category/components/categoryForm.tsx";

export const EditCategoryPage = () => {
    const { id } = useParams<{ id: string }>();

    const categoryId = id ? Number(id) : undefined;

    const { data, isLoading } = useQuery({
        queryKey: ['category', categoryId],
        queryFn: () => getCategory(categoryId!),
        enabled: !!categoryId && !isNaN(categoryId),
    });

    if (isLoading) return <div className="p-4 text-center">Loading...</div>;

    if (categoryId && isNaN(categoryId)) {
        return <div className="p-4 text-center text-red-500">Invalid Category ID</div>;
    }

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Categories / Edit</h1>
            <UpsertCategoryForm initialData={data} />
        </div>
    );
};