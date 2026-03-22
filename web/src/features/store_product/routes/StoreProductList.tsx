import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    useAllStoreProducts,
    useDeleteStoreProduct,
    useDownloadStoreProductPdf,
    useStoreProductPriceAndQuantity,
} from "@/features/store_product/hooks/useStoreProduct.ts";
import { type StoreProduct } from "@/features/store_product/types/types.ts";
import { useRole } from "@/hooks/useRole.ts";
import { toast } from "sonner";

type View = "all" | "prom" | "non-prom" | "search";

const SortToggle = ({
                        label,
                        value,
                        sortedBy,
                        onToggle,
                    }: {
    label: string;
    value: "name" | "quantity";
    sortedBy: "name" | "quantity" | undefined;
    onToggle: (value: "name" | "quantity") => void;
}) => {
    const isActive = sortedBy === value;
    return (
        <div className="flex items-center gap-1.5">
            <span className="text-sm text-zinc-700 font-medium">{label}</span>

            <button
                onClick={() => onToggle(value)}
                className={`relative inline-flex items-center w-11 h-6 rounded-full transition-colors duration-200 focus:outline-none ${
                    isActive ? "bg-green-500" : "bg-blue-200"
                }`}
            >
                <span className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow transition-transform duration-200 ${
                    isActive ? "translate-x-5" : "translate-x-0"
                }`} />
            </button>
        </div>
    );
};

export const StoreProductList = () => {
    const navigate = useNavigate();
    const { isManager, isCashier } = useRole();

    const [view, setView] = useState<View>("all");
    const [sortedBy, setSortedBy] = useState<"name" | "quantity" | undefined>(undefined);
    const [promFilter, setPromFilter] = useState<boolean | undefined>(undefined);
    const [currentIndex, setCurrentIndex] = useState(0);

    const [upcInput, setUpcInput] = useState("");
    const [searchUpc, setSearchUpc] = useState("");
    const isUpcSearch = isCashier && searchUpc.trim() !== "";

    const upcQuery = useStoreProductPriceAndQuantity(searchUpc);

    const { data, isLoading, error, isFetching } = useAllStoreProducts({
        sortedBy,
        prom: promFilter,
        page: currentIndex,
        enabled: !isUpcSearch,
    });

    const products = data?.content;
    const isLastPage = !(data?.hasNext ?? false);

    const deleteMutation = useDeleteStoreProduct();
    const pdfMutation = useDownloadStoreProductPdf();

    const resetPagination = () => setCurrentIndex(0);

    const handleSortToggle = (value: "name" | "quantity") => {
        setSortedBy(prev => prev === value ? undefined : value);
        setCurrentIndex(0);
        setUpcInput("");
        setSearchUpc("");
        resetPagination();
    };

    const handleSetView = (v: View) => {
        if (v !== view) {
            setView(v);
            setPromFilter(v === "prom" ? true : v === "non-prom" ? false : undefined);
            setUpcInput("");
            setSearchUpc("");
            resetPagination();
        }
    };

    const handleUpcSearch = () => {
        setSearchUpc(upcInput.trim());
        setPromFilter(undefined);
        setSortedBy(undefined);
        setView("all");
    };

    const handleDelete = (upc: string) => {
        toast("Видалити товар у магазині?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () =>
                    deleteMutation.mutate(upc, {
                        onSuccess: () => {
                            toast.success("Товар успішно видалено");
                            resetPagination();
                        },
                        onError: () => toast.error("Помилка під час видалення товару"),
                    }),
            },
            cancel: { label: "Скасувати", onClick: () => {} },
        });
    };

    const renderPagination = () => (
        <div className="flex justify-between items-center p-3 bg-zinc-50 text-xs">
            <button
                onClick={() => setCurrentIndex(prev => Math.max(0, prev - 1))}
                disabled={currentIndex === 0 || isFetching}
                className={`transition-opacity ${currentIndex === 0 || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
            >
                <div className="hover:scale-110 transition-transform flex justify-center w-full">
                    <img src="/src/logos/arrow-left.png" alt="prev" className="w-5 h-5" />
                </div>
            </button>
            <span className="text-zinc-500">Сторінка {currentIndex + 1}</span>
            <button
                onClick={() => { if (!isLastPage && !isFetching) setCurrentIndex(prev => prev + 1); }}
                disabled={isLastPage || isFetching}
                className={`transition-opacity ${isLastPage || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
            >
                <div className="hover:scale-110 transition-transform flex justify-center w-full">
                    <img src="/src/logos/arrow-right.png" alt="next" className="w-5 h-5" />
                </div>
            </button>
        </div>
    );

    return (
        <div className="bg-zinc-100 p-2 space-y-4">
            <div className="flex justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Товари в магазині</h2>
            </div>

            <div className="flex items-center justify-between gap-2 flex-wrap">
                <div className="flex items-center gap-5 flex-wrap">
                    <div className="flex items-center gap-2 flex-wrap">
                        {(["all", "prom", "non-prom"] as View[]).map((v) => (
                            <button
                                key={v}
                                onClick={() => handleSetView(v)}
                                className={`px-3 py-1.5 rounded text-sm border transition-colors ${
                                    view === v
                                        ? "bg-green-500 text-white border-white"
                                        : "bg-blue-200 border-blue-300 text-zinc-900 hover:bg-green-200 hover:border-green-200"
                                }`}
                            >
                                {v === "all" ? "Усі" : v === "prom" ? "Акційні" : "Неакційні"}
                            </button>
                        ))}
                    </div>

                    {isCashier && <SortToggle label="Сортувати за назвою" value="name" sortedBy={sortedBy} onToggle={handleSortToggle} />}
                    {isManager && <SortToggle label="Сортувати за кількістю" value="quantity" sortedBy={sortedBy} onToggle={handleSortToggle} />}

                    {(view === "prom" || view === "non-prom")
                        && isCashier && <SortToggle label="Сортувати за назвою" value="name" sortedBy={sortedBy} onToggle={handleSortToggle} />
                        && <SortToggle label="Сортувати за кількістю" value="quantity" sortedBy={sortedBy} onToggle={handleSortToggle} />}

                    {(view === "prom" || view === "non-prom")
                        && isManager && <SortToggle label="Сортувати за кількістю" value="quantity" sortedBy={sortedBy} onToggle={handleSortToggle} />
                        && <SortToggle label="Сортувати за назвою" value="name" sortedBy={sortedBy} onToggle={handleSortToggle} />}
                </div>

                {isCashier && (
                    <div className="relative flex-1 px-3">
                        <div className="relative flex items-center">
                            <input
                                value={upcInput}
                                onChange={e => setUpcInput(e.target.value)}
                                placeholder="Введіть UPC"
                                className="w-full border rounded px-3 py-1.5 text-sm pr-9 text-zinc-900"
                                onKeyDown={e => { if (e.key === "Enter") handleUpcSearch(); }}
                            />
                            <button
                                onClick={handleUpcSearch}
                                className="absolute right-1 top-1/2 -translate-y-1/2 w-6 h-6"
                            >
                                <img src="/src/logos/search.png" alt="search" className="w-6 h-6 hover:scale-110" />
                            </button>
                            {isUpcSearch && (
                                <button
                                    onClick={() => { setUpcInput(""); setSearchUpc(""); }}
                                    className="absolute right-8 top-1/2 -translate-y-1/2 text-red-500 text-sm px-1"
                                >
                                    ✕
                                </button>
                            )}
                        </div>
                    </div>
                )}

                {isManager && (
                    <div className="flex items-center">
                        <button onClick={() => navigate("/store-product/create")}>
                            <div className="hover:scale-110 transition-transform flex justify-center px-2">
                                <img src="/src/logos/add.png" alt="add" className="w-8 h-8" />
                            </div>
                        </button>
                        <button
                            onClick={() => pdfMutation.mutate()}
                            disabled={pdfMutation.isPending}
                            className="bg-zinc-700 text-white px-3 py-2 rounded hover:bg-zinc-800 text-xs whitespace-nowrap"
                        >
                            Друкувати звіт
                        </button>
                    </div>
                )}
            </div>

            {isUpcSearch && (
                <div>
                    {upcQuery.isLoading && <p className="text-zinc-500">Завантаження…</p>}
                    {upcQuery.error && (
                        <p className="text-red-500">Товар з UPC "{searchUpc}" не знайдено</p>
                    )}
                    {upcQuery.data && (
                        <div className="overflow-x-auto bg-white border border-blue-300 relative">
                            <table className="w-full text-xs border-collapse table-fixed border-b border-blue-300">
                                <thead>
                                <tr className="bg-blue-700 text-center text-white">
                                    <th className="px-3 py-2 font-semibold w-44 border border-blue-500">UPC</th>
                                    <th className="px-3 py-2 font-semibold w-50 border border-blue-500">Ціна продажу, грн</th>
                                    <th className="px-3 py-2 font-semibold w-50 border border-blue-500">Кількість</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr
                                    onClick={() => navigate(`/store-product/${searchUpc}`)}
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono border border-blue-200">{searchUpc}</td>
                                    <td className="px-3 py-2 border border-blue-200">{upcQuery.data.sellingPrice}</td>
                                    <td className="px-3 py-2 border border-blue-200">{upcQuery.data.productsNumber}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}

            {!isUpcSearch && (
                <>
                    {isLoading && <p className="text-zinc-500">Завантаження…</p>}
                    {error && (
                        <p className="text-red-500 text-sm">Помилка: {(error as Error).message}</p>
                    )}

                    {products && (
                        products.length === 0 ? (
                            <p className="text-zinc-400 text-sm">Товарів не знайдено</p>
                        ) : (
                            <div className="overflow-x-auto bg-white border border-blue-300 relative">
                                {isFetching && currentIndex > 0 && (
                                    <div className="absolute inset-0 bg-white/50 flex items-center justify-center z-10">
                                        <span className="text-blue-600 font-medium text-sm">Оновлення...</span>
                                    </div>
                                )}
                                <table className="w-full text-xs border-collapse table-fixed border-b border-blue-300">
                                    <thead>
                                    <tr className="bg-blue-700 text-center text-white">
                                        <th className="px-3 py-2 font-semibold w-44 border border-blue-500">UPC</th>
                                        <th className="px-3 py-2 font-semibold w-44 border border-blue-500">Назва</th>
                                        <th className="px-3 py-2 font-semibold w-50 border border-blue-500">Ціна продажу, грн</th>
                                        <th className="px-3 py-2 font-semibold w-50 border border-blue-500">Кількість</th>
                                        <th className="px-3 py-2 font-semibold w-32 border border-blue-500">Акційний товар</th>
                                        {isManager && <th className="px-1 py-2 w-6 border border-blue-500" />}
                                        {isManager && <th className="px-1 py-2 w-6 border border-blue-500" />}
                                        {isManager && <th className="px-1 py-2 w-6 border border-blue-500" />}
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {products.map((product: StoreProduct) => (
                                        <tr
                                            key={product.upc}
                                            onClick={() => navigate(`/store-product/${product.upc}`)}
                                            className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                        >
                                            <td className="px-3 py-2 font-mono border border-blue-200">{product.upc}</td>
                                            <td className="px-3 py-2 font-mono border border-blue-200">{product.productName}</td>
                                            <td className="px-3 py-2 border border-blue-200">{product.sellingPrice}</td>
                                            <td className="px-3 py-2 border border-blue-200">{product.productsNumber}</td>
                                            <td className="px-3 py-2 border border-blue-200">{product.promotionalProduct ? "Так" : "Ні"}</td>
                                            {isManager && (
                                                <td
                                                    className="px-1 py-2 border border-blue-200 text-center w-8"
                                                    onClick={e => e.stopPropagation()}
                                                >
                                                    <button
                                                        onClick={() => navigate(`/store-product/receive/${product.upc}`)}
                                                        title="Отримати нову партію"
                                                    >
                                                        <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                                            <img src="/src/logos/batch.png" alt="batch" className="h-4 w-5" />
                                                        </div>
                                                    </button>
                                                </td>
                                            )}
                                            {isManager && (
                                                <td
                                                    className="px-1 py-2 border border-blue-200 text-center w-8"
                                                    onClick={e => e.stopPropagation()}
                                                >
                                                    <button onClick={() => navigate(`/store-product/edit/${product.upc}`)}>
                                                        <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                                            <img src="/src/logos/edit.png" alt="edit" className="w-4 h-4" />
                                                        </div>
                                                    </button>
                                                </td>
                                            )}
                                            {isManager && (
                                                <td
                                                    className="px-1 py-2 border border-blue-200 text-center w-8"
                                                    onClick={e => e.stopPropagation()}
                                                >
                                                    <button onClick={() => handleDelete(product.upc)}>
                                                        <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                                            <img src="/src/logos/delete.png" alt="delete" className="w-4 h-4" />
                                                        </div>
                                                    </button>
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                                {renderPagination()}
                            </div>
                        )
                    )}
                </>
            )}
        </div>
    );
};
