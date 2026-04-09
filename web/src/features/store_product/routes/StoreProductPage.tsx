import { useParams } from "react-router-dom";
import { useStoreProduct } from "@/features/store_product/hooks/useStoreProduct.ts";
import { StoreProductComponent } from "@/features/store_product/components/StoreProductComponent.tsx";

export const StoreProductPage = () => {
    const { upc } = useParams<{ upc: string }>();

    const query = useStoreProduct(upc!);

    const isLoading = query.isLoading;
    const error = query.error;

    if (isLoading) return <div className="p-4 text-center">Завантаження...</div>;
    if (error) return <div className="p-4 text-red-500">Товар не знайдено</div>;

    if (query.data) {
        return (
            <div className="p-4">
                <StoreProductComponent data={query.data} />
            </div>
        );
    }

    return <div className="p-4 text-red-500">Помилка доступу</div>;
};
