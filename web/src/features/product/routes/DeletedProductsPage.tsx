import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useDeletedProducts } from "@/features/product/hooks/useProduct.ts";
import {SelectField, type SelectFieldProps} from "@/components/ui/SelectField.tsx";
import {useAllCategories} from "@/features/category/hooks/useCategory.ts";

// export const CheckSelect = (props: Omit<SelectFieldProps, 'name' | 'options' | 'valueAsNumber'>) => {
//     const { data: checks } = useAllChecks();
//     const options = checks?.map(c => ({ value: c.checkNumber })) ?? [];
//
//     return <SelectField name="checkNumber" options={options} valueAsNumber {...props} />;
// };

export const DeletedProductsPage = () => {
    const navigate = useNavigate();

    const [checkInput, setCheckInput] = useState("");
    const [searchCheck, setSearchCheck] = useState("");
    const [currentIndex, setCurrentIndex] = useState(0);

    const { data, isLoading, isError, isFetching } = useDeletedProducts(
        searchCheck, currentIndex, !!searchCheck
    );

    const products = data?.content;
    const isLastPage = !(data?.hasNext ?? false);

    const handleSearch = () => {
        setCurrentIndex(0);
        setSearchCheck(checkInput);
    };

    const handleClear = () => {
        setCheckInput("");
        setSearchCheck("");
        setCurrentIndex(0);
    };

    const handleNextPage = () => {
        if (isLastPage || isFetching) return;
        setCurrentIndex(prev => prev + 1);
    };

    const handlePrevPage = () => {
        setCurrentIndex(prev => Math.max(0, prev - 1));
    };

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Видалені товари, що існують у раніше створеному чеку</h2>
            </div>

            <div className="flex items-center justify-between gap-10">
                <button
                    onClick={() => navigate(-1)}
                    className="text-sm text-zinc-500 hover:text-zinc-800 transition-colors"
                >
                    ← До всіх товарів
                </button>

                <div className="relative flex-1">
                    <input
                        value={checkInput}
                        onChange={(e) => setCheckInput(e.target.value)}
                        placeholder="Укажіть номер чека"
                        className="w-full border rounded px-3 py-1.5 text-sm pr-9 text-zinc-900 bg-red-50"
                        onKeyDown={(e) => { if (e.key === "Enter") handleSearch(); }}
                    />
                    <button
                        onClick={handleSearch}
                        className="absolute right-1 top-1/2 -translate-y-1/2 w-6 h-6"
                    >
                        <img src="/src/logos/search.png" alt="search" className="w-6 h-6 hover:scale-110" />
                    </button>
                    {searchCheck && (
                        <button
                            onClick={handleClear}
                            className="absolute right-8 top-1/2 -translate-y-1/2 text-red-500 text-sm px-1"
                        >✕</button>
                    )}
                </div>
            </div>

            {isLoading && <p className="text-zinc-500">Завантаження...</p>}
            {isError && <p className="text-red-500 text-sm">Помилка завантаження</p>}

            {!searchCheck && (
                <p className="text-zinc-400 text-sm">Оберіть номер чека</p>
            )}

            {searchCheck && products && (
                products.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Нічого не знайдено для чека "{searchCheck}"</p>
                ) : (
                    <div className="overflow-x-auto bg-white border border-green-300 relative">
                        <table className="w-full text-xs border-collapse table-fixed border-b border-green-300">
                            <thead>
                            <tr className="bg-green-600 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-16 border border-green-700">ID</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-green-700">Назва</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-green-700">Виробник</th>
                                <th className="px-3 py-2 font-semibold w-48 border border-green-700">Характеристики</th>
                            </tr>
                            </thead>
                            <tbody>
                            {products.map((product) => (
                                <tr
                                    key={product.idProduct}
                                    onClick={() => navigate(`/products/${product.idProduct}`)}
                                    className="bg-green-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-green-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-green-300">{product.idProduct}</td>
                                    <td className="px-3 py-2 border border-green-300">{product.productName}</td>
                                    <td className="px-3 py-2 border border-green-300 truncate">{product.producer}</td>
                                    <td className="px-3 py-2 border border-green-300">{product.productCharacteristics}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>

                        <div className="flex justify-between items-center p-3 bg-zinc-50 text-xs">
                            <button
                                onClick={handlePrevPage}
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
                                disabled={isLastPage || isFetching}
                                className={`transition-opacity ${isLastPage || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
                            >
                                <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                    <img src="/src/logos/arrow-right.png" alt="next" className="w-5 h-5" />
                                </div>
                            </button>
                        </div>
                    </div>
                )
            )}
        </div>
    );
};
