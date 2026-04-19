import type { StoreProduct } from "@/features/store_product/types/types.ts";
import {useNavigate} from "react-router-dom";

interface Props {
    data: StoreProduct;
}

export const StoreProductComponent = ({ data }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Інформація про товар у магазині</h2>
            <p><span className="font-medium">UPC:</span> {data.upc}</p>

            {data.upcProm && (
                <div
                    onClick={() => navigate(`/store-product/${data.upcProm}`)}
                    title="Переглянути інформацію про акційний товар цього виду"
                    className="hover:text-red-700 cursor-pointer"
                >
                    <span className="font-medium">UPC акційного товару:</span> {data.upcProm}
                </div>)}
            <p><span className="font-medium">Товар:</span> #{data.idProduct} {data.productName}</p>
            <p><span className="font-medium">Ціна продажу:</span> {data.sellingPrice} грн</p>
            <p><span className="font-medium">Кількість одиниць:</span> {data.productsNumber}</p>
            {data.promotionalProduct && (
                <div className="flex items-left gap-2 font-medium py-2">
                    <img src="/src/logos/discount.png" alt="discount" className="h-6" />
                    <span>Акційний товар</span>
                </div>
            )}
        </div>
    );
};
