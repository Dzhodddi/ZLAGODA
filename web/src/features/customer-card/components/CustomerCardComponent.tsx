import type { CustomerCard } from '@/features/customer-card/types/types.ts';

interface Props {
    data: CustomerCard;
    isOwnProfile: boolean;
}

export const CustomerCardComponent = ({ data, isOwnProfile }: Props) => {
    const title = isOwnProfile ? "Мій профіль" : "Дані клієнта";
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">{title}</h2>
            <p><span className="font-medium">Номер карти:</span> {data.cardNumber}</p>
            <p><span className="font-medium">Ім'я:</span> {data.customerName}</p>
            <p><span className="font-medium">Прізвище:</span> {data.customerSurname}</p>
            <p><span className="font-medium">По батькові:</span> {data.customerPatronymic ?? "—"}</p>
            <p><span className="font-medium">Номер телефону:</span> {data.phoneNumber}</p>
            <p><span className="font-medium">Місто:</span> {data.city ?? "—"}</p>
            <p><span className="font-medium">Вулиця:</span> {data.street ?? "—"}</p>
            <p><span className="font-medium">Поштовий індекс:</span> {data.zipCode ?? "—"}</p>
            <p><span className="font-medium">Відсоток:</span> {data.customerPercent}%</p>
        </div>
    );
};
