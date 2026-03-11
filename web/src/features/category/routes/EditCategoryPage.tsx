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

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    if (categoryId && isNaN(categoryId)) {
        return <div className="p-4 text-center text-red-500">Неправильне ІД категорії</div>;
    }

    return (
        <div className="p-4">
            <UpsertCategoryForm initialData={data} />
        </div>
    );
};