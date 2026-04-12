import { useParams } from "react-router-dom";
import { useProduct } from "@/features/product/hooks/useProduct.ts";
import { ProductComponent } from "@/features/product/components/ProductComponent.tsx";
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";

export const ProductPage = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isLoading, isError } = useProduct(Number(id));

    if (isLoading)
        return <div className="p-4 text-center text-zinc-500">Завантаження...</div>;

    if (isError || !data)
        return (
            <NotFoundEntity
                title="Товар не знайдено"
                redirectTiList="/product"
                message={`Товар з ID ${id} не існує в базі даних`}
            />
        );

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <ProductComponent data={data} />
        </div>
    );
};
