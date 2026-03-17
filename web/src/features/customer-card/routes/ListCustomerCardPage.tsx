import { Link, useNavigate } from "react-router-dom";
import { useCustomerCardList, useDeleteCustomerCard } from "@/features/customer-card/hooks/useCustomerCard.ts";
import { toast } from "sonner";
import { useState } from "react";

type Cursor = {
    id: string;
    name: string;
};

export const CustomerCardListPage = () => {
    const [isSorted, setIsSorted] = useState(false);

    const [cursorHistory, setCursorHistory] = useState<Cursor[]>([{ id: "", name: "" }]);
    const [currentIndex, setCurrentIndex] = useState(0);

    const { data: customerCards, isLoading, isError, isFetching } = useCustomerCardList(
        isSorted
    );

    const deleteMutation = useDeleteCustomerCard();
    const navigate = useNavigate();

    const handleDelete = (cardNumber: string) => {
        toast("Видалити картку клієнта?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(cardNumber, {
                    onSuccess: () => toast.success("Картку успішно видалено"),
                    onError: () => toast.error("Помилка під час видалення картки"),
                }),
            },
            cancel: {
                label: "Скасувати",
                onClick: () => {},
            },
        });
    };

    const handleNextPage = () => {
        if (!customerCards || customerCards.length === 0) return;

        const lastItem = customerCards[customerCards.length - 1];

        if (!lastItem) return;

        const nextCursor: Cursor = {
            id: lastItem.cardNumber,
            name: lastItem.customerName
        };

        const nextIndex = currentIndex + 1;

        if (nextIndex >= cursorHistory.length) {
            setCursorHistory([...cursorHistory, nextCursor]);
        }

        setCurrentIndex(nextIndex);
    };

    const handlePrevPage = () => {
        setCurrentIndex((prev) => Math.max(0, prev - 1));
    };

    const handleSortToggle = () => {
        setIsSorted((prev) => !prev);
        setCurrentIndex(0);
        setCursorHistory([{ id: "", name: "" }]);
    };

    const isLastPage = customerCards ? customerCards.length < 10 : true;

    if (isLoading && currentIndex === 0) {
        return <div className="p-6 text-center text-zinc-500">Завантаження карток...</div>;
    }

    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження карток.</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Картки клієнтів</h2>
            </div>

            <div className="flex items-center justify-between">
                <div className="flex items-center gap-1.5">
                    <span className="text-sm text-zinc-700 font-medium">
                        Сортувати за ім'ям
                    </span>

                    <button
                        onClick={handleSortToggle}
                        className={`relative inline-flex items-center w-11 h-6 rounded-full transition-colors duration-200 focus:outline-none ${
                            isSorted ? "bg-green-500" : "bg-blue-200"
                        }`}
                    >
                        <span className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow transition-transform duration-200 ${
                            isSorted ? "translate-x-5" : "translate-x-0"
                        }`} />
                    </button>
                </div>
                <div className="flex items-center gap-5">
                    <Link to="/customer-card/create">
                        <div className="hover:scale-110 transition-transform flex justify-center">
                            <img src="/src/logos/add.png" alt="add" className="w-8 h-8" />
                        </div>
                    </Link>
                </div>
            </div>

            {customerCards?.length === 0 && currentIndex === 0 ? (
                <p className="text-zinc-400 text-sm">Карток не знайдено. Натисніть "Додати картку", щоб створити нову.</p>
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
                            <th className="px-3 py-2 font-semibold w-16 border border-blue-500 text-center">Номер</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">ПІБ</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Знижка (у відсотках %)</th>
                            <th className="px-1 py-2 font-semibold w-12 border border-blue-500"></th>
                            <th className="px-1 py-2 font-semibold w-12 border border-blue-500"></th>
                        </tr>
                        </thead>
                        <tbody>
                        {customerCards?.length === 0 ? (
                            <tr>
                                <td colSpan={4} className="px-3 py-8 text-center text-zinc-500 bg-white">
                                    Ви досягли кінця списку. Більше карток немає
                                </td>
                            </tr>
                        ) : (
                            customerCards?.map((card) => (
                                <tr
                                    key={card.cardNumber}
                                    onClick={() => navigate(`/customer-card/${card.cardNumber}`)}
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200">{card.cardNumber}</td>
                                    <td className="px-3 py-2 border border-blue-200 truncate ">{card.customerName + " " + card.customerSurname + " " + (card.customerPatronymic ? card.customerPatronymic : "")}</td>
                                    <td className="px-3 py-2 border border-blue-200 truncate ">{card.customerPercent}</td>
                                    <td className="px-1 py-2 border border-blue-200 text-center" onClick={(e) => e.stopPropagation()}>
                                        <button
                                            onClick={() => navigate(`/customer-card/edit/${card.cardNumber}`)}
                                            className="hover:scale-110 transition-transform flex justify-center w-full"
                                        >
                                            <img src="/src/logos/edit.png" alt="edit" className="w-4 h-4" />
                                        </button>
                                    </td>
                                    <td className="px-1 py-2 border border-blue-200 text-center" onClick={(e) => e.stopPropagation()}>
                                        <button
                                            onClick={() => handleDelete(card.cardNumber)}
                                            className="hover:scale-110 transition-transform flex justify-center w-full"
                                        >
                                            <img src="/src/logos/delete.png" alt="delete" className="w-4 h-4" />
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>

                    <div className="flex justify-between items-center p-3 bg-zinc-50 text-xs">
                        <button
                            onClick={handlePrevPage}
                            disabled={currentIndex === 0 || isFetching}
                            className={`transition-opacity ${currentIndex === 0 || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
                        >
                            <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                <img src="/src/logos/arrow-left.png" alt="edit" className="w-5 h-5" />
                            </div>
                        </button>

                        <span className="text-zinc-500">
                                Сторінка {currentIndex + 1}
                        </span>

                        <button
                            onClick={handleNextPage}
                            disabled={isLastPage || isFetching}
                            className={`transition-opacity ${isLastPage || isFetching ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
                        >
                            <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                <img src="/src/logos/arrow-right.png" alt="edit" className="w-5 h-5" />
                            </div>
                        </button>
                    </div>

                </div>
            )}
        </div>
    );
};