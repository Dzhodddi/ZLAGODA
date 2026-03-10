import { Link } from "react-router-dom";
import {useCategoryList, useDeleteCategory} from "@/features/category/hooks/useCategory.ts";
import {toast} from "sonner";

export const CategoryListPage = () => {
    const { data: categories, isLoading, isError } = useCategoryList();
    const deleteMutation = useDeleteCategory();

    const handleDelete = (id: number) => {
        toast.warning("Delete Category?", {
            description: "Are you sure you want to delete category?",
            action: {
                label: "Yes, Delete",
                onClick: () => {
                    deleteMutation.mutate(id)
                    toast.success("Successfully deleted category")
                }
            },
            cancel: {
                label: "Cancel",
                onClick: () => {},
            },
            duration: 6000,
        });
    };

    if (isLoading) {
        return <div className="p-6 text-center text-zinc-500">Loading categories...</div>;
    }

    if (isError) {
        console.log(categories)
        return <div className="p-6 text-center text-red-500">Failed to load categories.</div>;
    }

    return (
        <div className="p-6 max-w-5xl mx-auto text-zinc-900">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Categories</h1>
                <Link
                    to="/categories/new"
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors"
                >
                    + Add Category
                </Link>
            </div>

            <div className="bg-white rounded shadow-md overflow-hidden">
                <table className="w-full text-left border-collapse">
                    <thead className="bg-zinc-100 text-zinc-700">
                    <tr>
                        <th className="p-4 border-b font-semibold">ID</th>
                        <th className="p-4 border-b font-semibold">Category Name</th>
                        <th className="p-4 border-b font-semibold text-right">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {categories?.length === 0 ? (
                        <tr>
                            <td colSpan={3} className="p-6 text-center text-zinc-500">
                                No categories found. Click "Add Category" to create one.
                            </td>
                        </tr>
                    ) : (
                        categories?.map((category) => (
                            <tr key={category.categoryNumber} className="hover:bg-zinc-50 transition-colors">
                                <td className="p-4 border-b text-sm">{category.categoryNumber}</td>
                                <td className="p-4 border-b font-medium">{category.categoryName}</td>
                                <td className="p-4 border-b text-right space-x-4">

                                    <Link
                                        to={`/categories/edit/${category.categoryNumber}`}
                                        className="text-blue-600 hover:text-blue-800 font-medium hover:underline"
                                    >
                                        Edit
                                    </Link>

                                    <button
                                        onClick={() => handleDelete(category.categoryNumber)}
                                        disabled={deleteMutation.isPending}
                                        className="text-red-500 hover:text-red-700 font-medium hover:underline disabled:opacity-50"
                                    >
                                        Delete
                                    </button>

                                </td>
                            </tr>
                        ))
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};