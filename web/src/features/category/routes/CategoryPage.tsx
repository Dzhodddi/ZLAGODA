import {useParams} from "react-router-dom";
import {useCategory} from "@/features/category/hooks/useCategory.ts";
import {CategoryComponent} from "@/features/category/components/CategoryComponent.tsx";

export const CategoryPage = () => {
    const { id } = useParams<{ id: string }>();

    const categoryId = id ? Number(id) : undefined;

    if (!categoryId || isNaN(categoryId)) {
        return <div className="p-4 text-center text-red-500">Неправильний ІД категорії</div>;
    }

    const { data, isLoading, isError } = useCategory(categoryId);

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    if (isError || !data)
        return <div className="p-4 text-center text-red-500">Категорію не знайдено</div>;

    return (
        <div className="p-4">
            <CategoryComponent data={data} />
        </div>
    );
};
