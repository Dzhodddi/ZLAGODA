import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useRole } from "@/hooks/useRole.ts";
import { toast } from "sonner";
import {
    useProducts,
    useDeleteProduct,
    useDownloadProductPdf,
} from "@/features/product/hooks/useProduct.ts";
import { useAllCategories } from "@/features/category/hooks/useCategory.ts";
import {Combobox, type ComboboxOption} from "@/components/ui/ComboBox.tsx";

type View = "all" | "byName" | "byCategory";

const SortToggle = ({
                        label,
                        value,
                        sortedBy,
                        onToggle,
                    }: {
    label: string;
    value: "name";
    sortedBy: "name" | undefined;
    onToggle: (value: "name") => void;
}) => {
    const isActive = sortedBy === value;
    return (
        <div className="flex items-center gap-1.5">
            <span className="text-sm text-zinc-700 font-medium">{label}</span>

            <button
                onClick={() => onToggle(value)}
                title="Сортувати товари"
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

export const ProductList = () => {
    const navigate = useNavigate();
    const { isManager, isCashier } = useRole();

    const [view, setView] = useState<View>("all");

    const [nameInput, setNameInput] = useState("");
    const [searchName, setSearchName] = useState("");
    const [sortedBy, setSortedBy] = useState<"name" | undefined>(undefined);

    const [searchCategoryId, setSearchCategoryId] = useState<number | undefined>(undefined);

    const [currentIndex, setCurrentIndex] = useState(0);

    const { data: categories } = useAllCategories();

    const productsQuery = useProducts(
        searchName || undefined,
        searchCategoryId,
        currentIndex,
        sortedBy === "name",
    );

    const products = productsQuery.data?.content;
    const isFetching = productsQuery.isFetching;
    const isLastPage = !(productsQuery.data?.hasNext ?? false);

    const deleteMutation = useDeleteProduct();
    const pdfMutation = useDownloadProductPdf();

    const handleNextPage = () => {
        if (isLastPage || isFetching) return;
        setCurrentIndex(prev => prev + 1);
    };

    const handlePrevPage = () => {
        setCurrentIndex(prev => Math.max(0, prev - 1));
    };

    const handleSortToggle = (value: "name") => {
        setSortedBy(prev => prev === value ? undefined : value);
        setCurrentIndex(0);
    };

    const resetPagination = () => setCurrentIndex(0);

    const handleSetView = (v: View) => {
        if (v !== view) {
            setView(v);
            resetPagination();
        }
    };
    const options: ComboboxOption[] | undefined = categories?.map((c) => ({
        value: c.categoryNumber,
        label: c.categoryName,
    }));

    const handleDelete = (id: number) => {
        toast("Видалити товар?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(id, {
                    onSuccess: () => {
                        toast.success("Товар успішно видалений");
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
                onClick={handlePrevPage}
                title="Попередня сторінка"
                disabled={currentIndex === 0 || isFetching}
                className={`transition-opacity ${currentIndex === 0 || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
            >
                <div className="hover:scale-110 transition-transform flex justify-center w-full">
                    <img src="/src/logos/arrow-left.png" alt="prev" className="w-5 h-5" />
                </div>
            </button>
            <span className="text-zinc-500">Сторінка {currentIndex + 1}</span>
            <button
                onClick={handleNextPage}
                title="Наступна сторінка"
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
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Товари</h2>
            </div>

            <div className="flex flex-wrap items-center gap-5">
                {isManager && (
                    <button
                        onClick={() => navigate("/product/sold")}
                        className="px-3 py-1.5 rounded text-sm border transition-colors bg-blue-200 border-blue-300 text-zinc-900 hover:bg-green-200 hover:border-green-200"
                    >
                        Товари, що продавалися
                    </button>
                )}

                <SortToggle label="Сортувати за назвою" value="name" sortedBy={sortedBy} onToggle={handleSortToggle} />

                {isCashier && (
                    <div className="relative flex-1 min-w-[160px]">
                        <input
                            value={nameInput}
                            onChange={(e) => setNameInput(e.target.value)}
                            placeholder="Введіть назву"
                            title="Шукати інформацію про товар за його назвою"
                            className="w-full border rounded px-3 py-1.5 text-sm pr-9 text-zinc-900"
                            onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                    setSearchName(nameInput);
                                    setSearchCategoryId(undefined);
                                    handleSetView("byName");
                                }
                            }}
                        />
                        <button
                            onClick={() => { setSearchName(nameInput); handleSetView("byName"); }}
                            className="absolute right-1 top-1/2 -translate-y-1/2 w-6 h-6"
                        >
                            <img src="/src/logos/search.png" alt="search" className="w-6 h-6 hover:scale-110" />
                        </button>
                        {view === "byName" && (
                            <button
                                onClick={() => { setNameInput(""); setSearchName(""); handleSetView("all"); }}
                                title="Скинути пошук"
                                className="absolute right-8 top-1/2 -translate-y-1/2 text-red-500 text-sm px-1"
                            >✕</button>
                        )}
                    </div>
                )}

                <Combobox
                    options={options}
                    value={searchCategoryId}
                    onChange={(id) => {
                        setSearchName("");
                        setNameInput("");
                        setCurrentIndex(0);
                        setSearchCategoryId(id);
                    }}
                />

                {isManager && (
                    <div className="flex items-center gap-2 ml-auto">
                        <button onClick={() => navigate("/product/create")}
                                title="Додати новий товар"
                        >
                            <div className="hover:scale-110 transition-transform flex justify-center">
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

            {productsQuery.isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {productsQuery.error && (
                <p className="text-red-500 text-sm">Помилка: {(productsQuery.error as Error).message}</p>
            )}

            {products && (
                products.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Товарів не знайдено</p>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-xs border-collapse table-fixed border border-green-300">
                            <thead>
                            <tr className="bg-blue-700 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-12 border border-blue-500">ID</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-blue-500">Назва</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-blue-500">Виробник</th>
                                <th className="px-3 py-2 font-semibold w-70 border border-blue-500">Характеристики</th>
                                {isManager && <th className="px-1 py-2 font-semibold w-6 border border-blue-500" />}
                                {isManager && <th className="px-1 py-2 font-semibold w-6 border border-blue-500" />}
                            </tr>
                            </thead>
                            <tbody>
                            {products.map((product) => (
                                <tr
                                    key={product.idProduct}
                                    onClick={() => navigate(`/product/${product.idProduct}`)}
                                    title="Переглянути інформацію про товар"
                                    className="bg-blue-100 text-left border-t hover:bg-blue-200 text-zinc-900 cursor-pointer"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200 wrap-break-word">{product.idProduct}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{product.productName}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{product.producer}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{product.productCharacteristics}</td>

                                    {isManager && (
                                        <td className="px-2 py-2 border border-blue-200 text-center w-8"
                                            onClick={(e) => e.stopPropagation()}>
                                            <button onClick={() => navigate(`/product/edit/${product.idProduct}`)}
                                                    title="Редагувати товар"
                                            >
                                                <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                                    <img src="/src/logos/edit.png" alt="edit" className="w-4 h-4" />
                                                </div>
                                            </button>
                                        </td>
                                    )}

                                    {isManager && (
                                        <td className="px-2 py-2 border border-blue-200 text-center w-8"
                                            onClick={(e) => e.stopPropagation()}>
                                            <button onClick={() => handleDelete(product.idProduct)}
                                                    title="Видалити товар"
                                            >
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
        </div>
    );
};
