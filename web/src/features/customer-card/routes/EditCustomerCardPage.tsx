import { useParams } from "react-router-dom";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";
import {useCustomerCard} from "@/features/customer-card/hooks/useCustomerCard.ts";
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";

export const EditCustomerCardPage = () => {
    const { id } = useParams<{ id: string }>();

    const { data, isLoading, isError } = useCustomerCard(id!);

    if (isLoading)
        return <div className="p-6 text-center text-zinc-500">Завантаження...</div>;

    if (isError) {
        return (
            <NotFoundEntity
                title="Картку не знайдено"
                redirectTiList="/customer-card"
                message={`Клієнта з номером картки ${id} не існує в базі даних.`}
            />
        );
    }

    return (
        <div className="p-4">
            <UpsertCustomerCardForm initialData={data} />
        </div>
    );
};