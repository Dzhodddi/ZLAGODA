import { useState } from "react";
import type { CustomerCard } from '@/features/customer-card/types/types.ts';
import {useCustomerFavouriteProducts} from "@/features/customer-card/hooks/useCustomerCard.ts";

interface Props {
    data: CustomerCard;
    isOwnProfile: boolean;
}

const CustomerCardComponent = ({ data, isOwnProfile }: Props) => {
    const title = isOwnProfile ? "Мій профіль" : "Дані клієнта";

    const [showProducts, setShowProducts] = useState(false);

    const { data: products, isLoading, isError } = useCustomerFavouriteProducts(data.cardNumber, showProducts);

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">{title}</h2>
            <div className="space-y-1">
            <p><span className="font-medium">Номер карти:</span> {data.cardNumber}</p>
            <p><span className="font-medium">Прізвище:</span> {data.customerSurname}</p>
            <p><span className="font-medium">Ім'я:</span> {data.customerName}</p>
            <p><span className="font-medium">По батькові:</span> {data.customerPatronymic ?? "—"}</p>
            <p><span className="font-medium">Контактний телефон:</span> {data.phoneNumber}</p>
            <p><span className="font-medium">Місто:</span> {data.city ?? "—"}</p>
            <p><span className="font-medium">Вулиця:</span> {data.street ?? "—"}</p>
            <p><span className="font-medium">Поштовий індекс:</span> {data.zipCode ?? "—"}</p>
            <p><span className="font-medium">Відсоток на знижку:</span> {data.customerPercent} %</p>
            </div>

            <div className="mt-6 pt-4 border-t border-gray-200">
                <button
                    onClick={() => setShowProducts((prev) => !prev)}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                    {showProducts ? "Приховати" : "Детальніше"}
                </button>
            </div>

            {showProducts && (
                <div className="mt-6 animate-in fade-in slide-in-from-top-2 duration-300">
                    <h3 className="text-lg font-semibold mb-3 border-b pb-2">Улюблені товари клієнта </h3>

                    {isLoading ? (
                        <p className="text-zinc-500 text-sm">Завантаження історії...</p>
                    ) : isError ? (
                        <p className="text-red-500 text-sm">Не вдалося завантажити історію покупок</p>
                    ) : products && products.length > 0 ? (
                        <ul className="space-y-2">
                            {products.map((item, index) => (
                                <li
                                    key={`${item.productName}-${index}`}
                                    className="flex flex-wrap justify-between items-center bg-gray-50 p-3 rounded border border-gray-100 gap-4"
                                >
                                    <div className="flex flex-col w-64">
                                        <span><span className="font-medium text-gray-600">Назва:</span> {item.productName}</span>
                                    </div>
                                    <div className="w-36">
                                        <span><span className="font-medium text-gray-600">Ціна:</span> {item.totalPrice} грн.</span>
                                    </div>
                                    <div className="w-32">
                                        <span><span className="font-medium text-gray-600">Кількість:</span> {item.quantity} шт.</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-500 italic">Улюблені товари відсутні</p>
                    )}
                </div>
            )}
        </div>
    );
};
export default CustomerCardComponent