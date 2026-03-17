import { CustomerCardComponent } from '@/features/customer-card/components/CustomerCardComponent.tsx';
import { useParams } from 'react-router-dom';
import { useCustomerCard } from '@/features/customer-card/hooks/useCustomerCard.ts';

export const CustomerCardPage = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isLoading } = useCustomerCard(id!);

    if (isLoading) return <p>Завантаження...</p>;
    if (!data) return <p>Немає даних</p>;
    return (
        <div className="p-4">
            <CustomerCardComponent data={data} isOwnProfile={false} />
        </div>
    );
};