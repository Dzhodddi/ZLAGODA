import { usePopCategories } from "@/features/category/hooks/useCategory.ts";
import { useNavigate } from "react-router-dom";

export const TopCategoriesPage = () => {
    const { data: categories, isLoading, isError } = usePopCategories();
    const navigate = useNavigate();

    if (isLoading) {
        return <div className="p-6 text-center text-zinc-500">Завантаження...</div>;
    }
    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Дві найпопулярніші категорії товарів</h2>
            </div>

            <div className="flex items-center justify-between">
                <button
                    onClick={() => navigate(-1)}
                    className="text-sm text-zinc-500 hover:text-zinc-800 transition-colors"
                >
                    ← Назад
                </button>
            </div>

            <div className="overflow-x-auto bg-white border border-green-300 relative">
                <table className="w-full text-xs border-collapse table-fixed border-b border-green-300">
                    <thead>
                    <tr className="bg-green-600 text-center text-white">
                        <th className="px-3 py-2 font-semibold w-16 border border-green-700">Номер</th>
                        <th className="px-3 py-2 font-semibold border border-green-700">Назва</th>
                        <th className="px-3 py-2 font-semibold w-60 border border-green-700">Продано одиниць</th>
                    </tr>
                    </thead>
                    <tbody>
                    {categories?.map((category, index) => (
                        <tr key={index}
                            onClick={() => navigate(`/categories/${category.categoryNumber}`)}
                            className="bg-green-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-green-200 transition-colors">
                            <td className="px-3 py-2 font-mono text-xs border border-green-300">{category.categoryNumber}</td>
                            <td className="px-3 py-2 border border-green-300 truncate ">{category.categoryName}</td>
                            <td className="px-3 py-2 border border-green-300">{category.totalSold}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
