import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useSoldProducts } from "@/features/product/hooks/useProduct.ts";

export const SoldProductsPage = () => {
    const navigate = useNavigate();

    const [currentIndex, setCurrentIndex] = useState(0);

    const soldQuery = useSoldProducts(currentIndex);

    const products = soldQuery.data?.content;
    const isLastPage = !(soldQuery.data?.hasNext ?? false);
    const isFetching = soldQuery.isFetching;


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
                    Товари, одиниці яких продавалися в магазині хоча б один раз
                </h2>
            </div>

            <div className="flex items-center justify-between gap-10">
                <button
                    onClick={() => navigate(-1)}
                    className="text-sm text-zinc-500 hover:text-zinc-800 transition-colors"
                >
                    ← До всіх товарів
                </button>
            </div>

            {soldQuery.isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {soldQuery.error && (
                <p className="text-red-500 text-sm">Помилка: {(soldQuery.error as Error).message}</p>
            )}

            {products && (
                products.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Нічого не знайдено</p>
                ) : (
                    <div className="overflow-x-auto bg-white border border-green-300">
                        <table className="w-full text-xs border-collapse table-fixed border-b border-green-300">
                            <thead>
                            <tr className="bg-green-600 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-8 border border-green-700">ID</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-green-700">Назва</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-green-700">Кількість проданих одиниць</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-green-700">Сума проданих одиниць, грн</th>
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
                                    <td className="px-3 py-2 border border-green-300">{product.soldNumber}</td>
                                    <td className="px-3 py-2 border border-green-300">{product.totalSold}</td>
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
