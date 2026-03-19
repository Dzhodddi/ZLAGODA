import {type Product} from '@/features/product/types/types.ts';

interface Props {
    data: Product
}

export const ProductComponent = ({ data }: Props) => {
    const title = "Інформація про товар";
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">{title}</h2>
            <p><span className="font-medium">ID:</span> {data.idProduct}</p>
            <p><span className="font-medium">Назва:</span> {data.productName}</p>
            <p><span className="font-medium">Категорія:</span> {data.categoryNumber} {data.categoryName}</p>
            <p><span className="font-medium">Виробник:</span> {data.producer}</p>
            <p><span className="font-medium">Характеристики:</span> {data.productCharacteristics}</p>
        </div>
    );
};
