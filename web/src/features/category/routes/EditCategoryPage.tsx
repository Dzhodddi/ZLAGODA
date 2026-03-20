import { useParams } from "react-router-dom";
import { UpsertCategoryForm } from "@/features/category/components/categoryForm.tsx";
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";
import {useCategory} from "@/features/category/hooks/useCategory.ts";

export const EditCategoryPage = () => {
    const { id } = useParams<{ id: string }>();

    const categoryId = id ? Number(id) : undefined;

    if (!categoryId || isNaN(categoryId))
        return (
            <NotFoundEntity
                title="Категорію не знайдено"
                redirectTiList="/categories"
                message={`Категорію з ID ${id} не існує в базі даних.`}
            />
        );

    const { data, isLoading, isError } = useCategory(categoryId);

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    if (isError || !data)
        return (
            <NotFoundEntity
                title="Категорію не знайдено"
                redirectTiList="/categories"
                message={`Категорію з ID ${id} не існує в базі даних.`}
            />
        );

    return (
        <div className="p-4">
            <UpsertCategoryForm initialData={data} />
        </div>
    );
};