import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useDeletedProducts } from "@/features/product/hooks/useProduct.ts";
import { useAllChecks } from "@/features/checks/hooks/useCheck.ts";

export const DeletedProductsPage = () => {
    const navigate = useNavigate();

    const [searchCheck, setSearchCheck] = useState<string | undefined>(undefined);
    const [currentIndex, setCurrentIndex] = useState(0);

    const { data: checks } = useAllChecks();

    const deletedQuery = useDeletedProducts(searchCheck, currentIndex);

    const products = deletedQuery.data?.content;
    const isLastPage = !(deletedQuery.data?.hasNext ?? false);
    const isFetching = deletedQuery.isFetching;

    const handleCheckChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const val = e.target.value;
        setCurrentIndex(0);
        setSearchCheck(val ? val : undefined);
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
                <h2 className="text-xl font-bold text-zinc-900">
                    Знайти назви товарів, яких більше немає у магазині, за раніше оформленими чеками
                </h2>
            </div>

            <div className="flex items-center justify-between gap-10">
                <button
                    onClick={() => navigate(-1)}
                    className="text-sm text-zinc-500 hover:text-zinc-800 transition-colors"
                >
                    ← До всіх товарів
                </button>

                <div className="relative flex-1">
                    <select
                        value={searchCheck ?? ""}
                        onChange={handleCheckChange}
                        className="w-full border rounded px-3 py-1.5 text-sm text-zinc-900 bg-red-50 appearance-none pr-6 cursor-pointer"
                    >
                        <option value="">Оберіть номер чека</option>
                        {checks?.map((check) => (
                            <option key={check.checkNumber} value={check.checkNumber}>
                                {check.checkNumber}
                            </option>
                        ))}
                    </select>
                    <div className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2 text-zinc-900">▽</div>
                </div>
            </div>

            {!searchCheck && (
                <p className="text-zinc-400 text-sm">Укажіть чек</p>
            )}

            {deletedQuery.isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {deletedQuery.error && (
                <p className="text-red-500 text-sm">Помилка: {(deletedQuery.error as Error).message}</p>
            )}

            {searchCheck && products && (
                products.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Нічого не знайдено для чека "{searchCheck}"</p>
                ) : (
                    <div className="overflow-x-auto bg-white border border-green-300">
                        <table className="w-full text-xs border-collapse table-fixed border-b border-green-300">
                            <thead>
                            <tr className="bg-green-600 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-8 border border-green-700">ID</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-green-700">Назва</th>
                            </tr>
                            </thead>
                            <tbody>
                            {products.map((product) => (
                                <tr
                                    key={product.idProduct}
                                    onClick={() => navigate(`/product/${product.idProduct}`)}
                                    className="bg-green-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-green-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-green-300">{product.idProduct}</td>
                                    <td className="px-3 py-2 border border-green-300">{product.productName}</td>
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
