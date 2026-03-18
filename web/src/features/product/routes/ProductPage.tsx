import { useParams } from "react-router-dom";
import { useProduct } from "@/features/product/hooks/useProduct.ts";
import { ProductComponent } from "@/features/product/components/ProductComponent.tsx";

export const ProductPage = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isLoading, isError } = useProduct(Number(id));

    if (isLoading) return <p className="text-zinc-500 p-4">Завантаження...</p>;
    if (isError || !data) return <p className="text-red-500 p-4">Товар не знайдено</p>;

    return (
        <div className="bg-zinc-100 p-2 mx-auto space-y-4">
            <ProductComponent data={data} />
        </div>
    );
};
