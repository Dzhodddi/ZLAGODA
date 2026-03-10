import { useGetMe } from "@/features/employee/hooks/useEmployee.ts";

export const EmployeeMe = () => {
    const { data, isLoading } = useGetMe();

    if (isLoading) return <p>Завантаження...</p>;
    if (!data) return <p>Немає даних</p>;

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Мій профіль</h2>
            <p><span className="font-medium">ID:</span> {data.idEmployee}</p>
            <p><span className="font-medium">Прізвище:</span> {data.emplSurname}</p>
            <p><span className="font-medium">Ім'я:</span> {data.emplName}</p>
            <p><span className="font-medium">По батькові:</span> {data.emplPatronymic ?? "—"}</p>
            <p><span className="font-medium">Роль:</span> {data.role}</p>
            <p><span className="font-medium">Зарплата:</span> {data.salary}</p>
            <p><span className="font-medium">Дата народження:</span> {data.dateOfBirth}</p>
            <p><span className="font-medium">Дата прийому:</span> {data.dateOfStart}</p>
            <p><span className="font-medium">Телефон:</span> {data.phoneNumber}</p>
            <p><span className="font-medium">Місто:</span> {data.city}</p>
            <p><span className="font-medium">Вулиця:</span> {data.street}</p>
            <p><span className="font-medium">Поштовий індекс:</span> {data.zipCode}</p>
        </div>
    );
};
