import type {CheckItem} from "@/features/checks/types/types.ts";

interface Props {
    data: CheckItem;
}

export const CheckComponent = ({ data }: Props) => {
    const formattedDate = new Date(data.check.printDate).toLocaleString('uk-UA', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Інформація про чек</h2>

            <div className="space-y-2 mb-6">
                <p><span className="font-medium">Номер чеку:</span> {data.check.checkNumber}</p>
                <p><span className="font-medium">ID працівника:</span> {data.check.idEmployee}</p>
                <p><span className="font-medium">Номер картки клієнта:</span> {data.check.cardNumber}</p>
                <p><span className="font-medium">Дата друку:</span> {formattedDate}</p>
                <p><span className="font-medium">Сума чеку:</span> {data.check.sumTotal} грн</p>
                <p><span className="font-medium">ПДВ:</span> {data.check.vat} грн</p>
            </div>

            <div>
                <h3 className="text-lg font-semibold mb-3 border-b pb-2">Список товарів</h3>
                {data.products.length > 0 ? (
                    <ul className="space-y-2">
                        {data.products.map((product, index) => (
                            <li
                                key={`${product.name}-${index}`}
                                className="flex justify-between bg-gray-50 p-3 rounded border border-gray-100"
                            >
                                <span><span className="font-medium text-gray-600">Назва:</span> {product.name}</span>
                                <span><span className="font-medium text-gray-600">Кількість:</span> {product.quantity} шт.</span>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500 italic">Товари відсутні</p>
                )}
            </div>
        </div>
    );
};