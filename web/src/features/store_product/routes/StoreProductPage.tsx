import { useParams } from "react-router-dom";
import { useStoreProduct } from "@/features/store_product/hooks/useStoreProduct.ts";
import { StoreProductComponent } from "@/features/store_product/components/StoreProductComponent.tsx";
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";

export const StoreProductPage = () => {
    const { upc } = useParams<{ upc: string }>();

    const { data, isLoading, isError } = useStoreProduct(upc!);

    if (isLoading)
        return <div className="p-4 text-center text-zinc-500">Завантаження...</div>;

    if (isError || !data)
        return (
            <NotFoundEntity
                title="Товар у магазині не знайдено"
                redirectTiList="/store-product"
                message={`Товар у магазині з UPC ${upc} не існує в базі даних`}
            />
        );

    return (
        <div className="p-4">
            <StoreProductComponent data={data} />
        </div>
    );

    return <div className="p-4 text-red-500">Помилка доступу</div>;
};
