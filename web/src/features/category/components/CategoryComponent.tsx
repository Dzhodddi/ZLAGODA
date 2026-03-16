import type {Category} from "@/features/category/types/types.ts";

interface Props {
    data: Category
}

export const CategoryComponent = ({data}: Props) =>  {
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Інформація про категорію товару</h2>
            <p><span className="font-medium">ID:</span> {data.categoryNumber}</p>
            <p><span className="font-medium">Назва:</span> {data.categoryName}</p>
        </div>
    );
};
