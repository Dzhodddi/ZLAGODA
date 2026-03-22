import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {getStoreProduct} from "@/features/store_product/api/storeProductApi.ts";
import {UpsertStoreProductForm} from "@/features/store_product/components/storeProductForm.tsx";

export const EditStoreProductPage = () => {
    const { upc } = useParams<{ upc: string }>();

    const { data, isLoading } = useQuery({
        queryKey: ['store-product', upc],
        queryFn: () => getStoreProduct(upc!),
        enabled: !!upc,
    });

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    if (!data)
        return <div className="p-4 text-red-500">Товар не знайдено</div>;

    return (
        <div className="p-4">
            <UpsertStoreProductForm initialData={data} />
        </div>
    );
};