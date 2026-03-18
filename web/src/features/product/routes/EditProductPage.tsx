import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { getEmployee } from "@/features/employee/api/employeeApi.ts";
import { UpsertEmployeeForm } from "@/features/employee/components/employeeForm.tsx";
import {getProduct} from "@/features/product/api/productApi.ts";
import {UpsertProductForm} from "@/features/product/components/productForm.tsx";
import {useProduct} from "@/features/product/hooks/useProduct.ts";

export const EditProductPage = () => {
    const { id } = useParams<{ id: string }>();

    const { data, isLoading } = useQuery({
        queryKey: ['products', Number(id)],
        queryFn: () => getProduct(Number(id)!),
        enabled: !!id,
    });

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    return (
        <div className="p-4">
            <UpsertProductForm initialData={data} />
        </div>
    );
};
