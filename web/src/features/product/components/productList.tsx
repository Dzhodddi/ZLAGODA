import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useProducts, useDeleteProduct, useDownloadProductPdf } from "@/features/product/hooks/useProduct";
import { type Product } from "@/features/product/types/types";
import { useRole } from "@/hooks/useRole";

export const ProductList = () => {
    const navigate = useNavigate();
    const { isManager, isCashier } = useRole();

    const [nameInput, setNameInput]           = useState("");
    const [searchName, setSearchName]         = useState("");
    const [categoryInput, setCategoryInput]   = useState("");
    const [categoryFilter, setCategoryFilter] = useState<number | undefined>(undefined);

    const { data, isLoading, error } = useProducts(
        searchName || undefined,
        categoryFilter
    );
    const deleteMutation = useDeleteProduct();
    const pdfMutation    = useDownloadProductPdf();

    const clearFilters = () => {
        setNameInput("");
        setSearchName("");
        setCategoryInput("");
        setCategoryFilter(undefined);
    };

    const hasActiveFilter = !!searchName || !!categoryFilter;

    return (
        <div className="bg-white rounded shadow-md p-6 max-w-4xl mx-auto space-y-4">
            <div className="flex flex-wrap justify-between items-center gap-2">
                <h2 className="text-xl font-bold text-zinc-900">Товари</h2>
                <div className="flex gap-2 flex-wrap">
                    {isManager && (
                        <>
                            <button
                                onClick={() => navigate("/product/create")}
                                className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700 text-sm"
                            >
                                + Додати
                            </button>
                            <button
                                onClick={() => pdfMutation.mutate()}
                                disabled={pdfMutation.isPending}
                                className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 text-sm"
                            >
                                PDF
                            </button>
                        </>
                    )}
                </div>
            </div>

            <div className="flex gap-2 flex-wrap items-center text-black">
                {isCashier && (
                    <div className="flex gap-1">
                        <input
                            value={nameInput}
                            onChange={(e) => setNameInput(e.target.value)}
                            placeholder="Введіть назву"
                            className="border rounded px-2 py-1 text-sm w-40"
                            onKeyDown={(e) => {
                                if (e.key === "Enter") setSearchName(nameInput);
                            }}
                        />
                        <button
                            onClick={() => {
                                setSearchName(nameInput);
                                setCategoryFilter(undefined);
                                setCategoryInput("");
                            }}
                            className="bg-blue-500 text-white px-3 py-1 rounded text-sm"
                        >
                            Шукати
                        </button>
                    </div>
                )}

                <div className="flex gap-1">
                    <input
                        value={categoryInput}
                        onChange={(e) => setCategoryInput(e.target.value)}
                        placeholder="ID категорії"
                        type="number"
                        className="border rounded px-2 py-1 text-sm w-32"
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                const n = parseInt(categoryInput);
                                if (!isNaN(n)) {
                                    setCategoryFilter(n);
                                    setSearchName("");
                                    setNameInput("");
                                }
                            }
                        }}
                    />
                    <button
                        onClick={() => {
                            const n = parseInt(categoryInput);
                            if (!isNaN(n)) {
                                setCategoryFilter(n);
                                setSearchName("");
                                setNameInput("");
                            }
                        }}
                        className="bg-blue-500 text-white px-3 py-1 rounded text-sm"
                    >
                        Фільтр
                    </button>
                </div>

                {hasActiveFilter && (
                    <button onClick={clearFilters} className="text-red-500 text-sm px-2">
                        ✕
                    </button>
                )}
            </div>

            {isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {error && (
                <p className="text-red-500 text-sm">
                    Помилка: {(error as Error).message}
                </p>
            )}

            {data?.content && data.content.length === 0 && (
                <p className="text-zinc-400 text-sm">Товарів не знайдено.</p>
            )}

            {data?.content && data.content.length > 0 && (
                <table className="w-full text-sm border-collapse">
                    <thead>
                    <tr className="bg-blue-700 text-left text-white">
                        <th className="px-3 py-2 font-semibold">Назва</th>
                        <th className="px-3 py-2 font-semibold">Характеристики</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.content.map((product: Product, i: number) => (
                        <tr
                            key={i}
                            className="bg-blue-100 text-left border-t text-zinc-900"
                        >
                            <td className="px-3 py-2 font-medium">{product.productName}</td>
                            <td className="px-3 py-2 text-zinc-600 max-w-xs truncate">
                                {product.productCharacteristics}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};
