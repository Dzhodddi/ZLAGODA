import type {Employee} from "@/features/employee/types/types.ts";

interface Props {
    data: Employee
    isOwnProfile: boolean
}

export const EmployeeComponent = ({data, isOwnProfile}: Props) =>  {
    const title = isOwnProfile ? "Мій профіль" : "Дані працівника";
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">{title}</h2>
            <p><span className="font-medium">ID:</span> {data.idEmployee}</p>
            <p><span className="font-medium">Прізвище:</span> {data.emplSurname}</p>
            <p><span className="font-medium">Ім'я:</span> {data.emplName}</p>
            <p><span className="font-medium">По батькові:</span> {data.emplPatronymic ?? "—"}</p>
            <p><span className="font-medium">Посада:</span> {data.role == "MANAGER" ? "менеджер" : "касир"} </p>
            <p><span className="font-medium">Зарплата:</span> {data.salary}</p>
            <p><span className="font-medium">Дата народження:</span> {data.dateOfBirth}</p>
            <p><span className="font-medium">Дата початку роботи:</span> {data.dateOfStart}</p>
            <p><span className="font-medium">Контактний телефон:</span> {data.phoneNumber}</p>
            <p><span className="font-medium">Місто:</span> {data.city}</p>
            <p><span className="font-medium">Вулиця:</span> {data.street}</p>
            <p><span className="font-medium">Поштовий індекс:</span> {data.zipCode}</p>
        </div>
    );
};
