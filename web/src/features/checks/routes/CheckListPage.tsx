import { Link, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { useState } from "react";
import { useCheckList, useDeleteCheck } from "@/features/checks/hooks/useCheck.ts";
import { useRole } from "@/hooks/useRole.ts";

type Cursor = {
    checkNumber: string;
};

const getTodayDateString = () => new Date().toISOString().split("T")[0];
const getDecadeAgoDateString = () => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 10);
    return d.toISOString().split("T")[0];
};

export const CheckListPage = () => {
    const { isManager, isCashier } = useRole();

    const [startDate, setStartDate] = useState(getDecadeAgoDateString());
    const [endDate, setEndDate] = useState(getTodayDateString());
    const [idEmployee, setIdEmployee] = useState("");

    const [cursorHistory, setCursorHistory] = useState<Cursor[]>([{ checkNumber: "" }]);
    const [currentIndex, setCurrentIndex] = useState(0);

    const currentCursor = cursorHistory[currentIndex] ?? { checkNumber: "" };

    const { data: checks, isLoading, isError, isFetching } = useCheckList(
        // currentCursor.checkNumber,
        startDate!,
        endDate!,
        idEmployee || undefined
    );

    const deleteMutation = useDeleteCheck();
    const navigate = useNavigate();

    const handleDelete = (checkNumber: string) => {
        toast("Видалити чек?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(checkNumber, {
                    onSuccess: () => toast.success("Чек успішно видалено")
                }),
            },
            cancel: {
                label: "Скасувати",
                onClick: () => {},
            },
        });
    };

    const resetPagination = () => {
        setCurrentIndex(0);
        setCursorHistory([{ checkNumber: "" }]);
    };

    const handleNextPage = () => {
        if (!checks || checks.length === 0) return;

        const lastItem = checks[checks.length - 1];
        if (!lastItem) return;

        const nextCursor: Cursor = {
            checkNumber: lastItem.check.checkNumber
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

    const isLastPage = checks ? checks.length < 10 : true;

    const isDateInvalid = Boolean(startDate && endDate && new Date(startDate) > new Date(endDate));

    if (isLoading && currentIndex === 0) {
        return <div className="p-6 text-center text-zinc-500">Завантаження чеків...</div>;
    }

    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження чеків.</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">Список чеків</h2>
            </div>

            <div className="bg-white p-4 rounded shadow-sm border border-blue-200 flex flex-col md:flex-row gap-4 items-end justify-between">
                <div className="flex flex-wrap items-end gap-4">
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

                    <div className="flex flex-col">
                        <label className="text-xs text-zinc-600 font-medium mb-1">ID Працівника</label>
                        <input
                            type="text"
                            placeholder="Усі працівники"
                            value={idEmployee}
                            onChange={(e) => {
                                setIdEmployee(e.target.value);
                                resetPagination();
                            }}
                            className="border border-blue-300 rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 focus:ring-blue-500 w-36"
                        />
                    </div>

                </div>

                <div className="flex items-center gap-5 mb-1">
                    {isCashier && (
                        <Link to="/check/create">
                            <div className="hover:scale-110 transition-transform flex justify-center">
                                <img src="/src/logos/add.png" alt="add" className="w-8 h-8" title="Додати чек" />
                            </div>
                        </Link>
                    )}
                </div>
            </div>

            {isDateInvalid && (
                <p className="text-red-500 text-sm">Кінцева дата не може бути меншою за початкову.</p>
            )}

            {checks?.length === 0 && currentIndex === 0 ? (
                <p className="text-zinc-400 text-sm">Чеків за вказаними фільтрами не знайдено.</p>
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
                            <th className="px-3 py-2 font-semibold w-24 border border-blue-500 text-center">Номер</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Працівник</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Картка</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Дата</th>
                            <th className="px-3 py-2 font-semibold w-20 border border-blue-500 text-center">ПДВ</th>
                            {isManager &&
                                <th className="px-1 py-2 font-semibold w-10 border border-blue-500"></th>
                            }
                        </tr>
                        </thead>
                        <tbody>
                        {checks?.length === 0 ? (
                            <tr>
                                <td colSpan={6} className="px-3 py-8 text-center text-zinc-500 bg-white">
                                    Ви досягли кінця списку. Більше чеків немає
                                </td>
                            </tr>
                        ) : (
                            checks?.map((check) => (
                                <tr
                                    key={check.check.checkNumber}
                                    onClick={() => navigate(`/check/${check.check.checkNumber}`)}
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200 text-center">{check.check.checkNumber}</td>
                                    <td className="px-3 py-2 border border-blue-200 text-center">{check.check.idEmployee}</td>
                                    <td className="px-3 py-2 border border-blue-200 text-center truncate">{check.check.cardNumber || "-"}</td>
                                    <td className="px-3 py-2 border border-blue-200 text-center">
                                        {new Date(check.check.printDate).toLocaleString('uk-UA', {
                                            year: 'numeric', month: '2-digit', day: '2-digit',
                                            hour: '2-digit', minute: '2-digit'
                                        })}
                                    </td>
                                    <td className="px-3 py-2 border border-blue-200 text-center font-medium">{check.check.vat}</td>
                                    {isManager &&
                                        <td className="px-1 py-2 border border-blue-200 text-center" onClick={(e) => e.stopPropagation()}>
                                            <button
                                                onClick={() => handleDelete(check.check.checkNumber)}
                                                className="hover:scale-110 transition-transform flex justify-center w-full"
                                            >
                                                <img src="/src/logos/delete.png" alt="delete" className="w-4 h-4" />
                                            </button>
                                        </td>
                                    }
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