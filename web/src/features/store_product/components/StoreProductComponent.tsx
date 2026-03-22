import type { StoreProduct } from "@/features/store_product/types/types.ts";

interface Props {
    data: StoreProduct;
}

export const StoreProductComponent = ({ data }: Props) => {
    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Інформація про товар у магазині</h2>
            <p><span className="font-medium">UPC:</span> {data.upc}</p>
            {data.promotionalProduct && data.upcProm && (
                <p><span className="font-medium">UPC промо:</span> {data.upcProm}</p>
            )}
            <p><span className="font-medium">Товар:</span> {data.idProduct} {data.productName}</p>
            <p><span className="font-medium">Ціна продажу:</span> {data.sellingPrice}</p>
            <p><span className="font-medium">Кількість:</span> {data.productsNumber}</p>
        </div>
    );
};
