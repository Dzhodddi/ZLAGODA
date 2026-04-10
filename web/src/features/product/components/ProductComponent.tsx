import {type Product, type ProductWithPeriod} from '@/features/product/types/types.ts';
import {useState} from "react";
import {useProductSoldNumber} from "@/features/product/hooks/useProduct.ts";
import {useRole} from "@/hooks/useRole.ts";

interface Props {
    data: Product,
}

const getTodayDateString = () => new Date().toISOString().split("T")[0];
const getDecadeAgoDateString = () => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 10);
    return d.toISOString().split("T")[0];
};

export const ProductComponent = ({ data }: Props) => {
    const title = "Інформація про товар";

    const [showSoldNumber, setShowSoldNumber] = useState(false);

    const [startDate, setStartDate] = useState(getDecadeAgoDateString());
    const [endDate, setEndDate] = useState(getTodayDateString());
    const isDateInvalid = Boolean(startDate && endDate && new Date(startDate) > new Date(endDate));

    const { isManager } = useRole();

    const { data: soldNumber,
        isError,
        isLoading } =
        useProductSoldNumber(
            data.idProduct,
            startDate ?? "",
            endDate ?? "",
            showSoldNumber && !isDateInvalid);

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">{title}</h2>
            <div className="space-y-1">
            <p><span className="font-medium">ID:</span> {data.idProduct}</p>
            <p><span className="font-medium">Назва:</span> {data.productName}</p>
            <p><span className="font-medium">Категорія:</span> {data.categoryNumber} {data.categoryName}</p>
            <p><span className="font-medium">Виробник:</span> {data.producer}</p>
            <p><span className="font-medium">Характеристики:</span> {data.productCharacteristics}</p>
            </div>

            {isManager && (
                <div className="mt-6 pt-4 border-t border-gray-200">
                    <button
                        onClick={() => setShowSoldNumber((prev) => !prev)}
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors text-sm font-medium"
                    >
                        {showSoldNumber ? "Приховати кількість проданих одиниць" : "Кількість проданих одиниць"}
                    </button>
                </div>
            )}
            {showSoldNumber && (
                <div className="mt-6 animate-in fade-in slide-in-from-top-2 duration-300">
                    <h3 className="text-lg font-semibold mb-3 border-b pb-2">Кількість одиниць товару, проданих за певний період</h3>

                    <div className="flex flex-wrap items-center gap-2">
                        <label className="text-xs text-zinc-600 font-medium mb-1">Від дати</label>
                        <input
                            type="date"
                            value={startDate}
                            title={"Оберіть початкову дату для отримання кількості проданих одиниць цього товару"}
                            onChange={(e) => {
                                setStartDate(e.target.value);
                            }}
                            className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                        />

                        <label className="text-xs text-zinc-600 font-medium mb-1 ml-5">До дати</label>
                        <input
                            type="date"
                            min={startDate}
                            value={endDate}
                            title={"Оберіть кінцеву дату для отримання кількості проданих одиниць цього товару"}
                            onChange={(e) => {
                                setEndDate(e.target.value);
                            }}
                            className={`border rounded px-2 py-1.5 text-sm text-zinc-800 focus:outline-none focus:ring-1 ${isDateInvalid ? 'border-red-500 focus:ring-red-500' : 'border-blue-300 focus:ring-blue-500'}`}
                        />
                    </div>

                    { isDateInvalid ? (
                        <p className="py-3 text-red-500 text-sm">Кінцева дата не може бути меншою за початкову</p>
                    ) : isLoading ? (
                        <p className="text-zinc-500 text-sm">Завантаження даних про продані одиниці товару...</p>
                    ) : isError ? (
                    <p className="text-red-500 text-sm">Не вдалося завантажити дані про продаж цього товару в магазині</p>
                    ) : soldNumber && soldNumber.soldNumber > 0 ? (
                        <p className="font-medium text-center text-blue-700 text-6xl py-3">
                            {soldNumber.soldNumber}
                        </p>
                    ) : (
                        <p className="text-gray-500 italic py-5 text-center">
                            Одиниці цього товару не продавалися в магазині за цей період
                        </p>
                    )}
                </div>
            )}
        </div>
    )
};
