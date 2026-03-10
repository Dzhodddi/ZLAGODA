import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    useAllStoreProducts,
    useDeleteStoreProduct,
    useDownloadStoreProductPdf
} from "@/features/store_product/hooks/useStoreProduct.ts";
import { type StoreProduct } from "@/features/store_product/types/types.ts";
import { useRole } from "@/hooks/useRole.ts";

export const StoreProductList = () => {
    const navigate = useNavigate();
    const { isManager, isCashier } = useRole();

    const [sortedBy, setSortedBy] = useState<"name" | "quantity" | undefined>(undefined);
    const [promFilter, setPromFilter] = useState<boolean | undefined>(undefined);

    const { data, isLoading, error } = useAllStoreProducts(sortedBy, promFilter);
    const deleteMutation = useDeleteStoreProduct();
    const pdfMutation = useDownloadStoreProductPdf();

    return (
        <div className="bg-white rounded shadow-md p-6 max-w-4xl mx-auto space-y-4">
            <div className="flex flex-wrap justify-between items-center gap-2">
                <h2 className="text-xl font-bold text-zinc-900">Товари в магазині</h2>
                <div className="flex gap-2 flex-wrap">
                    {isManager && (
                        <>
                            <button
                                onClick={() => navigate("/store-product/create")}
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
                    <button
                        onClick={() => setSortedBy(sortedBy === "name" ? undefined : "name")}
                        className={`px-3 py-1 rounded text-sm border transition-colors ${
                            sortedBy === "name" ? "bg-zinc-700 text-white" : "border-zinc-300"
                        }`}
                    >
                        За назвою
                    </button>
                )}
                {isManager && (
                    <button
                        onClick={() => setSortedBy(sortedBy === "quantity" ? undefined : "quantity")}
                        className={`px-3 py-1 rounded text-sm border transition-colors ${
                            sortedBy === "quantity" ? "bg-zinc-700 text-white" : "border-zinc-300"
                        }`}
                    >
                        За кількістю
                    </button>
                )}
                <button
                    onClick={() => setPromFilter(promFilter === true ? undefined : true)}
                    className={`px-3 py-1 rounded text-sm border transition-colors ${
                        promFilter === true ? "bg-zinc-700 text-white" : "border-zinc-300"
                    }`}
                >
                    Акційні
                </button>
                <button
                    onClick={() => setPromFilter(promFilter === false ? undefined : false)}
                    className={`px-3 py-1 rounded text-sm border transition-colors ${
                        promFilter === false ? "bg-zinc-700 text-white" : "border-zinc-300"
                    }`}
                >
                    Неакційні
                </button>
                {(sortedBy || promFilter !== undefined) && (
                    <button
                        onClick={() => { setSortedBy(undefined); setPromFilter(undefined); }}
                        className="text-red-500 text-sm px-2"
                    >
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
                        <th className="px-3 py-2 font-semibold">UPC</th>
                        <th className="px-3 py-2 font-semibold">Ціна</th>
                        <th className="px-3 py-2 font-semibold">Кількість</th>
                        <th className="px-3 py-2 font-semibold">Акційний</th>
                        {isManager && <th className="px-3 py-2" />}
                    </tr>
                    </thead>
                    <tbody>
                    {data.content.map((product: StoreProduct) => (
                        <tr
                            key={product.upc}
                            onClick={() => isManager && navigate(`/store-product/edit/${product.upc}`)}
                            className={`bg-blue-100 text-left border-t text-zinc-900 ${isManager ? "cursor-pointer" : ""}`}
                        >
                            <td className="px-3 py-2 font-mono text-xs">{product.upc}</td>
                            <td className="px-3 py-2">{product.sellingPrice}</td>
                            <td className="px-3 py-2">{product.productsNumber}</td>
                            <td className="px-3 py-2">{product.promotionalProduct ? "Так" : "Ні"}</td>
                            {isManager && (
                                <td
                                    className="px-3 py-2"
                                    onClick={(e) => e.stopPropagation()}
                                >
                                    <button
                                        onClick={() => deleteMutation.mutate(product.upc)}
                                        disabled={deleteMutation.isPending}
                                        className="text-red-600"
                                    >
                                        ✕
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};
