import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    useAllEmployees,
    useAllCashiers,
    useDeleteEmployee,
    useDownloadEmployeePdf,
    useEmployeePhoneAndAddress
} from "@/features/employee/hooks/useEmployee";
import { type Employee } from "@/features/employee/types/types";
import { useRole } from "@/hooks/useRole";
import { toast } from "sonner";

type View = "all" | "cashiers" | "search";

export const EmployeeList = () => {
    const navigate = useNavigate();
    const { isManager } = useRole();

    const [view, setView] = useState<View>("all");
    const [surnameInput, setSurnameInput] = useState("");
    const [searchSurname, setSearchSurname] = useState("");

    const allQuery = useAllEmployees();
    const cashierQuery = useAllCashiers();
    const searchQuery = useEmployeePhoneAndAddress(searchSurname);
    const deleteMutation = useDeleteEmployee();
    const pdfMutation = useDownloadEmployeePdf();

    const activeQuery =
        view === "cashiers" ? cashierQuery
            : view === "search"  ? searchQuery
                : allQuery;

    const employees = view === "search"
        ? undefined
        : (activeQuery.data?.content as Employee[] | undefined);

    const contacts = view === "search"
        ? searchQuery.data?.content
        : undefined;

    const handleDelete = (idEmployee: string) => {
        toast("Видалити працівника?", {
            actionButtonStyle: { backgroundColor: "#e45757", color: "white" },
            cancelButtonStyle: { color: "#5c5c5c" },
            action: {
                label: "ТАК",
                onClick: () => deleteMutation.mutate(idEmployee, {
                    onSuccess: () => toast.success("Працівник успішно видалений"),
                    onError: () => toast.error("Помилка під час видалення працівника"),
                }),
            },
            cancel: {
                label: "Скасувати",
                onClick: () => {},
            },
        });
    };

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <div className="flex flex-wrap justify-between items-center gap-2">
                <h2 className="text-xl font-bold text-zinc-900">Працівники</h2>
                <div className="flex gap-2 flex-wrap">
                    {isManager && (
                        <>
                            <button
                                onClick={() => navigate("/employee/create")}
                            >
                                <div className="relative w-8 h-8 group">
                                    <img src="/src/logos/add.png" alt="add" className="w-8 h-8 group-hover:opacity-0" />
                                    <img src="/src/logos/add-hover.png" alt="add" className="w-8 h-8 absolute inset-0 opacity-0 group-hover:opacity-100" />
                                </div>
                            </button>
                            <button
                                onClick={() => pdfMutation.mutate()}
                                disabled={pdfMutation.isPending}
                                className="bg-zinc-700 text-white px-3 py-1 rounded hover:bg-zinc-800 text-xs"
                            >
                                Видрукувати звіт про всіх працівників
                            </button>
                        </>
                    )}
                </div>
            </div>

            <div className="flex gap-2 flex-wrap text-black">
                {(["all", "cashiers"] as View[]).map((v) => (
                    <button
                        key={v}
                        onClick={() => setView(v)}
                        className={`px-3 py-1 rounded text-sm border transition-colors ${
                            view === v
                                ? "bg-zinc-700 text-white"
                                : "border-zinc-300"
                        }`}
                    >
                        {v === "all" ? "Усі" : "Касири"}
                    </button>
                ))}

            <div className="ml-auto flex gap-2 items-center">
                <div className="relative">
                    <input
                        value={surnameInput}
                        onChange={(e) => setSurnameInput(e.target.value)}
                        placeholder="Введіть прізвище"
                        className="border rounded px-2 py-1 text-sm w-58 pr-9"
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                setSearchSurname(surnameInput);
                                setView("search");
                            }
                        }}
                    />
                    <button
                        onClick={() => {
                            setSearchSurname(surnameInput);
                            setView("search");
                        }}
                        className="absolute right-1 top-1/2 -translate-y-1/2 w-6 h-6 group"
                    >
                        <img src="/src/logos/search.png" alt="search" className="w-6 h-6 group-hover:opacity-0" />
                        <img src="/src/logos/search-hover.png" alt="search" className="w-6 h-6 absolute inset-0 opacity-0 group-hover:opacity-100" />
                    </button>
                </div>
                {view === "search" && (
                    <button
                        onClick={() => {
                            setSurnameInput("");
                            setSearchSurname("");
                            setView("all");
                        }}
                        className="text-red-500 text-sm px-2"
                    >
                        ✕
                    </button>
                )}
            </div>
            </div>

            {activeQuery.isLoading && <p className="text-zinc-500">Завантаження…</p>}
            {activeQuery.error && (
                <p className="text-red-500 text-sm">
                    Помилка: {(activeQuery.error as Error).message}
                </p>
            )}

            {view === "search" && contacts && (
                contacts.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Нічого не знайдено для працівника_ці "{searchSurname}"</p>
                ) : (
                    <table className="w-full text-xs border-collapse table-fixed border border-blue-300">
                        <thead>
                        <tr className="bg-blue-700 text-center text-white">
                            <th className="px-3 py-2 font-semibold w-16 border border-blue-500">ID</th>
                            <th className="px-3 py-2 font-semibold w-32 border border-blue-500">Контактний телефон</th>
                            <th className="px-3 py-2 font-semibold w-48 border border-blue-500">Адреса</th>
                        </tr>
                        </thead>
                        <tbody>
                        {contacts.map((c, i) => (
                            <tr key={i}
                                onClick={() => navigate(`/employee/${c.idEmployee}`)}
                                className="bg-blue-100 text-left border-t hover:bg-blue-200 text-zinc-900 cursor-pointer">
                                <td className="px-3 py-2 border border-blue-200 wrap-break-word">{c.idEmployee}</td>
                                <td className="px-3 py-2 border border-blue-200 wrap-break-word">{c.phoneNumber}</td>
                                <td className="px-3 py-2 border border-blue-200 wrap-break-word">{c.city}, {"вул."} {c.street}, {c.zipCode}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )
            )}

            {view !== "search" && employees && (
                employees.length === 0 ? (
                    <p className="text-zinc-400 text-sm">Працівників не знайдено</p>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-xs border-collapse table-fixed border border-blue-300">
                            <thead>
                            <tr className="bg-blue-700 text-center text-white">
                                <th className="px-3 py-2 font-semibold w-16 border border-blue-500">ID</th>
                                <th className="px-3 py-2 font-semibold w-40 border border-blue-500">ПІБ</th>
                                <th className="px-3 py-2 font-semibold w-24 border border-blue-500">Посада</th>
                                <th className="px-3 py-2 font-semibold w-24 border border-blue-500">Зарплата, грн</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-blue-500">Дата народження</th>
                                <th className="px-3 py-2 font-semibold w-36 border border-blue-500">Дата початку роботи</th>
                                <th className="px-3 py-2 font-semibold w-32 border border-blue-500">Контактний телефон</th>
                                <th className="px-3 py-2 font-semibold w-48 border border-blue-500">Адреса</th>
                                {isManager && <th className="px-3 py-2 w-8 border border-blue-500 border-r-blue-700" />}
                                {isManager && <th className="px-3 py-2 w-8 border border-blue-500" />}
                            </tr>
                            </thead>
                            <tbody>
                            {employees.map((emp) => (
                                <tr key={emp.idEmployee}
                                    onClick={() => navigate(`/employee/${emp.idEmployee}`)}
                                    className="bg-blue-100 text-left border-t hover:bg-blue-200 text-zinc-900 cursor-pointer">
                                    <td className="px-3 py-2 font-mono text-xs border border-blue-200 wrap-break-word">{emp.idEmployee}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">
                                        {emp.emplSurname} {emp.emplName} {emp.emplPatronymic ?? ""}
                                    </td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">
                                        {emp.role === "MANAGER" ? "Менеджер" : "Касир"}
                                    </td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{emp.salary}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{emp.dateOfBirth}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{emp.dateOfStart}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{emp.phoneNumber}</td>
                                    <td className="px-3 py-2 border border-blue-200 wrap-break-word">{emp.city}, {"вул."} {emp.street}, {emp.zipCode}</td>
                                    {isManager && (
                                        <td className="px-2 py-2 border border-blue-200 text-center w-8" onClick={(e) => e.stopPropagation()}>
                                            <button onClick={() => navigate(`/employee/edit/${emp.idEmployee}`)}>
                                                <div className="relative w-4 h-4 group">
                                                    <img src="/src/logos/edit.png" alt="edit" className="w-4 h-4 group-hover:opacity-0" />
                                                    <img src="/src/logos/edit-hover.png" alt="edit" className="w-4 h-4 absolute inset-0 opacity-0 group-hover:opacity-100" />
                                                </div>
                                            </button>
                                        </td>
                                    )}
                                    {isManager && (
                                        <td className="px-2 py-2 border border-blue-200 text-center w-8" onClick={(e) => e.stopPropagation()}>
                                            <button onClick={() => handleDelete(emp.idEmployee)}>
                                                <div className="relative w-4 h-4 group">
                                                    <img src="/src/logos/delete.png" alt="delete" className="w-4 h-4 group-hover:opacity-0" />
                                                    <img src="/src/logos/delete-hover.png" alt="delete" className="w-4 h-4 absolute inset-0 opacity-0 group-hover:opacity-100" />
                                                </div>
                                            </button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )
            )}
        </div>
    );
};