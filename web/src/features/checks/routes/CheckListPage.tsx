import { Link, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { useState } from "react";
import { useCheckList, useDeleteCheck, useCheckTotalSum, useTodayCheckList } from "@/features/checks/hooks/useCheck.ts";
import { useRole } from "@/hooks/useRole.ts";
import { useDownloadCheckPdf } from "@/features/checks/hooks/useCheck.ts";
import { PAGE_SIZE } from "@/constants/constants.ts";

type Cursor = {
    checkNumber: string;
};

const getTomorrowDateString = () => {
    const d = new Date()
    d.setHours(d.getHours() + 24);
    return d.toISOString().split("T")[0];
}

const getThreeYearsAgoDateString = () => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 3);
    return d.toISOString().split("T")[0];
};

export const CheckListPage = () => {
    const { isManager, isCashier } = useRole();

    const [startDate, setStartDate] = useState(getThreeYearsAgoDateString());
    const [endDate, setEndDate] = useState(getTomorrowDateString());
    const [idEmployee, setIdEmployee] = useState("");

    const [isShowTodayOnly, setIsShowTodayOnly] = useState(false);

    const [cursorHistory, setCursorHistory] = useState<Cursor[]>([{ checkNumber: "" }]);
    const [currentIndex, setCurrentIndex] = useState(0);

    const currentCursor = cursorHistory[currentIndex] ?? { checkNumber: "" };

    const {
        data: allChecks,
        isLoading: isAllLoading,
        isError: isAllError,
        isFetching: isAllFetching
    } = useCheckList(
        startDate!,
        endDate!,
        idEmployee || undefined,
        currentCursor.checkNumber,
        { enabled: !isShowTodayOnly },
    );

    const {
        data: todayChecks,
        isLoading: isTodayLoading,
        isError: isTodayError,
        isFetching: isTodayFetching
    } = useTodayCheckList(
        isShowTodayOnly,
        currentCursor.checkNumber,
    );

    const checks = isShowTodayOnly ? todayChecks : allChecks;
    const isLoading = isShowTodayOnly ? isTodayLoading : isAllLoading;
    const isError = isShowTodayOnly ? isTodayError : isAllError;
    const isFetching = isShowTodayOnly ? isTodayFetching : isAllFetching;

    const { data: totalSum } = useCheckTotalSum(
        startDate!,
        endDate!,
        idEmployee || undefined,
        !isCashier && !isShowTodayOnly
    );

    const deleteMutation = useDeleteCheck();
    const pdfMutation = useDownloadCheckPdf();

    const navigate = useNavigate();

    const isDateInvalid = Boolean(startDate && endDate && new Date(startDate) > new Date(endDate));
    const isStartTooOld = Boolean(startDate && new Date(startDate) < new Date(getThreeYearsAgoDateString()!));

    const lastCheck = checks && checks.length > 0 ? checks[checks.length - 1] : null;
    const nextCheckNumber = lastCheck?.checkNumber ?? "";
    const canPrefetch = !!checks && checks.length === PAGE_SIZE;

    const { data: nextPageAllChecks } = useCheckList(
        startDate || "",
        endDate || "",
        idEmployee || undefined,
        nextCheckNumber,
        { enabled: !isShowTodayOnly && canPrefetch && !isDateInvalid }
    );

    const { data: nextPageTodayChecks } = useTodayCheckList(
        isShowTodayOnly && canPrefetch,
        nextCheckNumber,
    );

    const nextPageChecks = isShowTodayOnly ? nextPageTodayChecks : nextPageAllChecks;

    const handleDelete = (checkNumber: string) => {
        toast("Видалити чек?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(checkNumber, {
                    onSuccess: () => toast.success("Чек успішно видалений")
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
            checkNumber: lastItem.checkNumber
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

    const isLastPage =
        (checks ? checks.length < PAGE_SIZE : true) ||
        (nextPageChecks !== undefined && nextPageChecks.length === 0);

    if (isLoading && currentIndex === 0) {
        return <div className="p-6 text-center text-zinc-500">Завантаження чеків...</div>;
    }

    if (isError) {
        return <div className="p-6 text-center text-red-500">Помилка завантаження чеків</div>;
    }

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-center items-center">
                <h2 className="text-xl font-bold text-zinc-900">
                    {isShowTodayOnly ? "Чеки за сьогодні" : "Список чеків"}
                </h2>
            </div>

            <div className="bg-white p-4 rounded shadow-sm border border-blue-200 flex flex-col md:flex-row gap-4 items-end justify-between">
                <div className="flex flex-wrap items-center gap-5">
                    {isCashier &&
                        <div className="flex items-center gap-2 mb-1 mr-4">
                            <span className="text-sm text-zinc-700 font-medium">Тільки за сьогодні</span>
                            <button
                                onClick={() => {
                                    setIsShowTodayOnly(!isShowTodayOnly);
                                    resetPagination();
                                }}
                                className={`relative inline-flex items-center w-11 h-6 rounded-full transition-colors duration-200 focus:outline-none ${
                                    isShowTodayOnly ? "bg-green-500" : "bg-blue-200"
                                }`}
                            >
                                <span className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow transition-transform duration-200 ${
                                    isShowTodayOnly ? "translate-x-5" : "translate-x-0"
                                }`} />
                            </button>
                        </div>
                    }

                    {!isShowTodayOnly && (
                        <>
                            <div className="flex flex-col">
                                <label className="text-xs text-zinc-600 font-medium mb-1">Від дати</label>
                                <input
                                    type="date"
                                    value={startDate}
                                    title={"Оберіть початкову дату для пошуку чеків"}
                                    onChange={(e) => {
                                        setStartDate(e.target.value);
                                        resetPagination();
                                    }}
                                    max={getThreeYearsAgoDateString()}
                                    className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                                />
                            </div>

                            <div className="flex flex-col">
                                <label className="text-xs text-zinc-600 font-medium mb-1">До дати</label>
                                <input
                                    type="date"
                                    min={startDate}
                                    value={endDate}
                                    title={"Оберіть кінцеву дату для пошуку чеків"}
                                    onChange={(e) => {
                                        setEndDate(e.target.value);
                                        resetPagination();
                                    }}
                                    className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                                />
                            </div>
                        </>
                    )}
                    {isManager &&
                        <div className="flex flex-col">
                            <label className="text-xs text-zinc-600 font-medium mb-1">
                                ID касира/-ки
                            </label>
                            <input
                                type="text"
                                placeholder="Усі касири"
                                title="Шукати чек за ID касира/-ки, який(-а) його надрукував(-ла)"
                                value={idEmployee}
                                onChange={(e) => {
                                    setIdEmployee(e.target.value);
                                    resetPagination();
                                }}
                                className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 w-36 ${isShowTodayOnly && !idEmployee ? 'border-red-400 focus:ring-red-400' : 'border-blue-300 focus:ring-blue-500'}`}
                            />
                        </div>
                    }
                    {!isCashier && !isShowTodayOnly && !isDateInvalid && (
                        <div className="flex justify-end px-2">
                            <span className="text-sm font-semibold text-zinc-800 bg-white border border-blue-200 px-3 py-1.5 rounded shadow-sm">
                                Загальна сума чеків: {" "}
                                <span className="text-green-600">{Number(totalSum || 0).toFixed(2)} грн</span>
                            </span>
                        </div>
                    )}
                </div>

                <div className="flex items-center gap-2 ml-auto">
                    {isCashier && (
                        <Link to="/check/create">
                            <div className="hover:scale-110 transition-transform flex justify-center">
                                <img src="/src/logos/add.png" alt="add" className="w-8 h-8" title="Додати чек" />
                            </div>
                        </Link>
                    )}
                </div>
                <button
                    onClick={() => pdfMutation.mutate()}
                    disabled={pdfMutation.isPending}
                    className="bg-zinc-700 text-white px-3 py-2 rounded hover:bg-zinc-800 text-xs whitespace-nowrap"
                >
                    Друкувати звіт
                </button>
            </div>

            {!isShowTodayOnly && isDateInvalid && (
                <p className="text-red-500 text-sm">Кінцева дата не може бути меншою за початкову</p>
            )}
            {isStartTooOld && <p className="text-red-500 text-sm">Початкова дата не може бути старішою за 3 роки.</p>}

            {checks?.length === 0 && currentIndex === 0 ? (
                <p className="text-zinc-500 text-sm text-center bg-white p-4 rounded border border-blue-200">
                    {isShowTodayOnly ? "Чеків за сьогодні не знайдено" : "Чеків не знайдено"}
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
                            <th className="px-3 py-2 font-semibold w-46 border border-blue-500 text-center">Номер</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">ID касира/-ки</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Картка клієнта/-ки</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Дата</th>
                            <th className="px-3 py-2 font-semibold border border-blue-500 text-center">Сума, грн</th>
                            <th className="px-3 py-2 font-semibold w-40 border border-blue-500 text-center">ПДВ, грн</th>
                            {isManager &&
                                <th className="px-1 py-2 font-semibold w-10 border border-blue-500"></th>
                            }
                        </tr>
                        </thead>
                        <tbody>
                        {checks?.length === 0 ? (
                            <tr>
                                <td colSpan={isManager ? 7 : 6} className="px-3 py-8 text-center text-zinc-500 bg-white">
                                    Ви досягли кінця списку. Більше чеків немає
                                </td>
                            </tr>
                        ) : (
                            checks?.map((check) => (
                                <tr
                                    key={check.checkNumber}
                                    onClick={() => navigate(`/check/${check.checkNumber}`)}
                                    title="Переглянути інформацію про чек"
                                    className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer hover:bg-blue-200 transition-colors"
                                >
                                    <td className="px-3 py-2 font-medium border border-blue-200">{check.checkNumber}</td>
                                    <td className="px-3 py-2 border border-blue-200">{check.idEmployee}</td>
                                    <td className="px-3 py-2 border border-blue-200 truncate">{check.cardNumber || "-"}</td>
                                    <td className="px-3 py-2 border border-blue-200">{check.printDate}</td>
                                    <td className="px-3 py-2 border border-blue-200">{check.sumTotal}</td>
                                    <td className="px-3 py-2 border border-blue-200">{check.vat}</td>
                                    {isManager &&
                                        <td className="px-1 py-2 border border-blue-200 text-center" onClick={(e) => e.stopPropagation()}>
                                            <button
                                                onClick={() => handleDelete(check.checkNumber)}
                                                className="hover:scale-110 transition-transform flex justify-center w-full"
                                                title="Видалити чек"
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
                            title="Попередня сторінка"
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
                            disabled={isLastPage || isFetching || (!isShowTodayOnly && isDateInvalid)}
                            title="Наступна сторінка"
                            className={`transition-opacity ${isLastPage || isFetching || (!isShowTodayOnly && isDateInvalid) ? "opacity-30 cursor-not-allowed" : "opacity-100"}`}
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