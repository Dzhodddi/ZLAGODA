import { useParams } from 'react-router-dom';
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";
import {useCheck} from "@/features/checks/hooks/useCheck.ts";
import {CheckComponent} from "@/features/checks/components/CheckComponent.tsx";

export const CheckPage = () => {
    const { id } = useParams<{ id: string }>();

    const { data, isLoading, isError } = useCheck(id!);

    if (isLoading)
        return <div className="p-6 text-center text-zinc-500">Завантаження...</div>;

    if (isError) {
        return (
            <NotFoundEntity
                title="Чек не знайдено"
                redirectTiList="/check"
                message={`Чек з номером ${id} не існує в базі даних.`}
            />
        );
    }

    if (!data) return <p className="p-6 text-center text-zinc-500">Немає даних</p>;

    return (
        <div className="p-4">
            <CheckComponent data={data}/>
        </div>
    );
};