import { CustomerCardComponent } from '@/features/customer-card/components/CustomerCardComponent.tsx';
import { useParams } from 'react-router-dom';
import { useCustomerCard } from '@/features/customer-card/hooks/useCustomerCard.ts';
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";

export const CustomerCardPage = () => {
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

    if (!data) return <p className="p-6 text-center text-zinc-500">Немає даних</p>;

    return (
        <div className="p-4">
            <CustomerCardComponent data={data} isOwnProfile={false} />
        </div>
    );
};