import { useNavigate } from "react-router-dom";
import {useEffect, useState} from "react";
import { useSaleList } from "@/features/sales/hooks/useSale.ts";
import {PAGE_SIZE} from "@/constants/constants.ts";

type Cursor = {
    checkNumber: string | undefined;
    upc: string | undefined
};

const getTodayDateString = () => new Date().toISOString().split("T")[0];
const getDecadeAgoDateString = () => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 10);
    return d.toISOString().split("T")[0];
};

export const SaleListPage = () => {
    const [startDate, setStartDate] = useState(getDecadeAgoDateString());
    const [endDate, setEndDate] = useState(getTodayDateString());

    const [cursorHistory, setCursorHistory] = useState<Cursor[]>([{ checkNumber: undefined, upc: undefined}]);
    const [currentIndex, setCurrentIndex] = useState(0);

    const currentCursor = cursorHistory[currentIndex] ?? { checkNumber: undefined, upc: undefined };

    const navigate = useNavigate();

    const {
        data: sales,
        isLoading,
        isError,
        isFetching
    } = useSaleList(startDate!, endDate!, currentCursor.checkNumber, currentCursor.upc);

    const isDateInvalid = Boolean(startDate && endDate && new Date(startDate) > new Date(endDate));

    const lastItem = sales && sales.length > 0 ? sales[sales.length - 1] : null;

    const nextPageCursor: Cursor = lastItem
        ? { checkNumber: lastItem.checkNumber, upc: lastItem.upc }
        : { checkNumber: undefined, upc: undefined };

    const { data: nextPageSales } = useSaleList(
        startDate || "",
        endDate || "",
        nextPageCursor.checkNumber,
        nextPageCursor.upc,
        { enabled: !!sales && sales.length === PAGE_SIZE && !isDateInvalid }
    );

    const [hasNoMore, setHasNoMore] = useState(false);

    const resetPagination = () => {
        setCurrentIndex(0);
        setCursorHistory([{ checkNumber: undefined, upc: undefined }]);
        setHasNoMore(false);
    };

    const isLastPage =
        hasNoMore ||
        (sales ? sales.length < PAGE_SIZE : true) ||
        (nextPageSales !== undefined && nextPageSales.length === 0);

    const handleNextPage = () => {
        if (!sales || sales.length < PAGE_SIZE) {
            setHasNoMore(true);
            return;
        }

        const lastItem = sales[sales.length - 1];
        if (!lastItem) return;

        const nextCursor: Cursor = {
            checkNumber: lastItem.checkNumber,
            upc: lastItem.upc
        };

        const nextIndex = currentIndex + 1;

        if (nextIndex >= cursorHistory.length) {
            setCursorHistory(prev => [...prev, nextCursor]);
        }

        setCurrentIndex(nextIndex);
    };

    const handlePrevPage = () => {
        setCurrentIndex((prev) => Math.max(0, prev - 1));
        setHasNoMore(false);
    };

    useEffect(() => {
        if (!isFetching && sales?.length === 0 && currentIndex > 0) {
            setCurrentIndex(prev => prev - 1);
            setHasNoMore(true);
        }
    }, [sales, isFetching]);

    if (isLoading && currentIndex === 0) {
        return <div className="p-6 text-center text-zinc-500">Завантаження продажів...</div>;
    }

    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження продажів</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">
                    Список продажів
                </h2>
            </div>

            <div className="bg-white p-4 rounded shadow-sm border border-blue-200 flex flex-col md:flex-row gap-4 items-end justify-between">
                <div className="flex flex-wrap items-center gap-5">

                    <div className="flex flex-col">
                        <label className="text-xs text-zinc-600 font-medium mb-1">Від дати</label>
                        <input
                            type="date"
                            value={startDate}
                            onChange={(e) => {
                                setStartDate(e.target.value);
                                resetPagination();
                            }}
                            className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                        />
                    </div>

                    <div className="flex flex-col">
                        <label className="text-xs text-zinc-600 font-medium mb-1">До дати</label>
                        <input
                            type="date"
                            min={startDate}
                            value={endDate}
                            onChange={(e) => {
                                setEndDate(e.target.value);
                                resetPagination();
                            }}
                            className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                        />
                    </div>

                </div>
            </div>

            {isDateInvalid && (
                <p className="text-red-500 text-sm">Кінцева дата не може бути меншою за початкову</p>
            )}

            {sales?.length === 0 && currentIndex === 0 ? (
                <p className="text-zinc-400 text-sm text-center bg-white p-4 rounded border border-blue-200">
                    Продажів за вказаний період не знайдено
                </p>
            ) : (
                <div className="overflow-x-auto bg-white border border-blue-300 relative">
                    {isFetching && currentIndex > 0 && (
                        <div className="absolute inset-0 bg-white/50 flex items-center justify-center z-10">
                            <span className="text-blue-600 font-medium text-sm">Оновлення...</span>
                        </div>
                    )}

                    <table className="w-full text-xs border-collapse table-fixed border-b border-blue-300">
                        <thead>
                        <tr className="bg-blue-700 text-left text-white">
                            <th className="px-3 py-2 font-semibold w-32 border border-blue-500 text-center">Номер чеку</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">UPC Товару</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Кількість</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Ціна продажу</th>
                        </tr>
                        </thead>
                        <tbody>

                        {sales?.map((sale) => (
                                <tr
                                    key={`${sale.upc}-${sale.checkNumber}`}
                                    onClick={() => navigate(`/check/${sale.checkNumber}`)}
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200 text-center">{sale.checkNumber}</td>
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200 text-center">{sale.upc}</td>
                                    <td className="px-3 py-2 border border-blue-200 text-center">{sale.productNumber} шт.</td>
                                    <td className="px-3 py-2 border border-blue-200 text-center font-medium">{sale.sellingPrice} грн</td>
                                </tr>
                            ))
                        }
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

                        <span className="text-zinc-500">
                            Сторінка {currentIndex + 1}
                        </span>

                        <button
                            onClick={handleNextPage}
                            disabled={isLastPage || isFetching || isDateInvalid}
                            className={`transition-opacity ${isLastPage || isFetching || isDateInvalid ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
                        >
                            <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                <img src="/src/logos/arrow-right.png" alt="next" className="w-5 h-5" />
                            </div>
                        </button>
                    </div>

                </div>
            )}
        </div>
    );
};