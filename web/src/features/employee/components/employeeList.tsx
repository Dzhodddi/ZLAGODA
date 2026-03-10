import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    useAllEmployees,
    useAllCashiers,
    useDeleteEmployee,
    useDownloadEmployeePdf,
    useEmployeePhoneAndAddress,
} from "@/features/employee/hooks/useEmployee";
import { type Employee } from "@/features/employee/types/types";
import { useRole } from "@/hooks/useRole";

type View = "all" | "cashiers" | "search";

export const EmployeeList = () => {
    const navigate = useNavigate();
    const { isManager } = useRole();

    const [view, setView] = useState<View>("all");
    const [surnameInput, setSurnameInput] = useState("");
    const [searchSurname, setSearchSurname] = useState("");

    const allQuery     = useAllEmployees();
    const cashierQuery = useAllCashiers();
    const searchQuery  = useEmployeePhoneAndAddress(searchSurname);
    const deleteMutation = useDeleteEmployee();
    const pdfMutation    = useDownloadEmployeePdf();

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

    return (
        <div className="bg-white rounded shadow-md p-6 max-w-4xl mx-auto space-y-4">
            <div className="flex flex-wrap justify-between items-center gap-2">
                <h2 className="text-xl font-bold text-zinc-900">Працівники</h2>
                <div className="flex gap-2 flex-wrap">
                    {isManager && (
                        <>
                            <button
                                onClick={() => navigate("/employee/create")}
                                className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700 text-sm"
                            >
                                + Додати
                            </button>
                            <button
                                onClick={() => pdfMutation.mutate()}
                                disabled={pdfMutation.isPending}
                                className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 text-sm"
                            >
                                PDF
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

                <div className="flex gap-1 ml-auto">
                    <input
                        value={surnameInput}
                        onChange={(e) => setSurnameInput(e.target.value)}
                        placeholder="Введіть прізвище"
                        className="border rounded px-2 py-1 text-sm w-44"
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
                        className="bg-blue-500 text-white px-3 py-1 rounded text-sm"
                    >
                        Шукати
                    </button>
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
                    <p className="text-zinc-400 text-sm">Нічого не знайдено для "{searchSurname}"</p>
                ) : (
                    <table className="w-full text-sm border-collapse">
                        <thead>
                        <tr className="bg-zinc-100 text-left">
                            <th className="px-3 py-2 font-semibold">Телефон</th>
                            <th className="px-3 py-2 font-semibold">Місто</th>
                            <th className="px-3 py-2 font-semibold">Вулиця</th>
                        </tr>
                        </thead>
                        <tbody>
                        {contacts.map((c, i) => (
                            <tr key={i} className="border-t">
                                <td className="px-3 py-2">{c.phoneNumber}</td>
                                <td className="px-3 py-2">{c.city}</td>
                                <td className="px-3 py-2">{c.street}</td>
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
                    <table className="w-full text-sm border-collapse">
                        <thead>
                        <tr className="bg-blue-700 text-left text-white">
                            <th className="px-3 py-2 font-semibold">ID</th>
                            <th className="px-3 py-2 font-semibold">ПІБ</th>
                            <th className="px-3 py-2 font-semibold">Роль</th>
                            <th className="px-3 py-2 font-semibold">Телефон</th>
                            {isManager && <th className="px-3 py-2" />}
                        </tr>
                        </thead>
                        <tbody>
                        {employees.map((emp) => (
                            <tr
                                key={emp.idEmployee}
                                onClick={() => navigate(`/employee/edit/${emp.idEmployee}`)}
                                className="bg-blue-100 text-left border-t text-zinc-900 cursor-pointer"
                            >
                                <td className="px-3 py-2 font-mono text-xs">{emp.idEmployee}</td>
                                <td className="px-3 py-2">
                                    {emp.emplSurname} {emp.emplName}{" "}
                                    {emp.emplPatronymic ?? ""}
                                </td>
                                <td className="px-3 py-2">
                                    {emp.role === "MANAGER" ? "Менеджер" : "Касир"}
                                </td>
                                <td className="px-3 py-2">{emp.phoneNumber}</td>
                                {isManager && (
                                    <td
                                        className="px-3 py-2"
                                        onClick={(e) => e.stopPropagation()}
                                    >
                                        <button
                                            onClick={() => deleteMutation.mutate(emp.idEmployee)}
                                            disabled={deleteMutation.isPending}
                                            className="text-red-600"
                                        >
                                            ✕
                                        </button>
                                    </td>
                                )}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )
            )}
        </div>
    );
};
