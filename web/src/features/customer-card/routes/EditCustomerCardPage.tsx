import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {getCustomerCard} from "@/features/customer-card/api/customerCardApi.ts";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";

export const EditCustomerCardPage = () => {
    const { id } = useParams<{ id: string }>();

    const { data, isLoading } = useQuery({
        queryKey: ['customer_cards', id],
        queryFn: () => getCustomerCard(id!),
        enabled: !!id ,
    });

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    return (
        <div className="p-4">
            <UpsertCustomerCardForm initialData={data} />
        </div>
    );
};