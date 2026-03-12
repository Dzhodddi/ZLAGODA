import { Link, useNavigate } from "react-router-dom";
import { useCategoryList, useDeleteCategory } from "@/features/category/hooks/useCategory.ts";
import { toast } from "sonner";

export const CategoryListPage = () => {
    const { data: categories, isLoading, isError } = useCategoryList();
    const deleteMutation = useDeleteCategory();
    const navigate = useNavigate();

    const handleDelete = (categoryNumber: number) => {
        toast("Видалити категорію?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(categoryNumber, {
                    onSuccess: () => toast.success("Категорію успішно видалено"),
                    onError: () => toast.error("Помилка під час видалення категорії"),
                }),
            },
            cancel: {
                label: "Скасувати",
                onClick: () => {},
            },
        });
    };

    if (isLoading) {
        return <div className="p-6 text-center text-zinc-500">Завантаження категорій...</div>;
    }

    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження категорій.</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-between items-center gap-2">
                <h2 className="text-xl font-bold text-zinc-900">Категорії</h2>
                <div className="flex gap-2 flex-wrap">
                    <Link
                        to="/categories/create"
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors"
                    >
                        + Додати категорію
                    </Link>
                </div>
            </div>

            <div className="flex gap-2 flex-wrap text-black">
                <div className="flex gap-1 ml-auto">
                    <input
                        placeholder="Введіть назву категорії"
                        className="border rounded px-2 py-1 text-sm w-44"
                    />
                    <button
                        className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700"
                    >
                        Шукати
                    </button>
                </div>
            </div>

            {isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {isError && (
                <p className="text-red-500 text-sm">
                    Помилка: Завантаження категорій не вдалося.
                </p>
            )}

            {categories?.length === 0 ? (
                <p className="text-zinc-400 text-sm">Категорій не знайдено. Натисніть "Додати категорію", щоб створити нову.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="w-full text-xs border-collapse table-fixed border border-blue-300">
                        <thead>
                            <tr className="bg-blue-700 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-16 border border-blue-500">Номер</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-blue-500">Назва</th>
                                <th className="px-3 py-2 font-semibold w-24 border border-blue-500"></th>
                                <th className="px-3 py-2 font-semibold w-24 border border-blue-500"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories?.map((category) => (
                                <tr
                                    key={category.categoryNumber}
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200">{category.categoryNumber}</td>
                                    <td className="px-3 py-2 border border-blue-200">{category.categoryName}</td>
                                    <td className="px-2 py-2 border border-blue-200 text-center w-8" onClick={(e) => e.stopPropagation()}>
                                        <button onClick={() => navigate(`/categories/edit/${category.categoryNumber}`)}>
                                            <img src="/src/logos/edit.png" alt="edit" className="w-4 h-4" />
                                        </button>
                                    </td>
                                    <td className="px-2 py-2 border border-blue-200 text-center w-8" onClick={(e) => e.stopPropagation()}>
                                        <button onClick={() => handleDelete(category.categoryNumber)}>
                                            <img src="/src/logos/delete.png" alt="delete" className="w-4 h-4" />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};