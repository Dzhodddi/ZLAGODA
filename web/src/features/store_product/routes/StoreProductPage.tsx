import { useParams } from "react-router-dom";
import { useStoreProduct } from "@/features/store_product/hooks/useStoreProduct.ts";
import { useStoreProductPriceAndQuantity } from "@/features/store_product/hooks/useStoreProduct.ts";
import { useRole } from "@/hooks/useRole.ts";
import { StoreProductComponent } from "@/features/store_product/components/StoreProductComponent.tsx";

export const StoreProductPage = () => {
    const { upc } = useParams<{ upc: string }>();
    const { isManager, isCashier } = useRole();

    const managerQuery = useStoreProduct(isManager ? upc! : "");
    const cashierQuery = useStoreProductPriceAndQuantity(isCashier ? upc! : "");

    const isLoading = managerQuery.isLoading || cashierQuery.isLoading;
    const error = managerQuery.error || cashierQuery.error;

    if (isLoading) return <div className="p-4 text-center">Завантаження...</div>;
    if (error) return <div className="p-4 text-red-500">Товар не знайдено</div>;

    if (isManager && managerQuery.data) {
        return (
            <div className="p-4">
                <StoreProductComponent data={managerQuery.data} />
            </div>
        );
    }

    if (isCashier && cashierQuery.data) {
        return (
            <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto mt-4">
                <h2 className="text-xl font-bold mb-4">Інформація про товар у магазині</h2>
                <p><span className="font-medium">UPC:</span> {upc}</p>
                <p><span className="font-medium">Ціна продажу, грн:</span> {cashierQuery.data.sellingPrice}</p>
                <p><span className="font-medium">Кількість:</span> {cashierQuery.data.productsNumber}</p>
            </div>
        );
    }

    return <div className="p-4 text-red-500">Помилка доступу</div>;
};
