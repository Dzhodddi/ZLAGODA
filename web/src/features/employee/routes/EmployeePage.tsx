import {EmployeeComponent} from "@/features/employee/components/EmployeeComponent.tsx";
import {useParams} from "react-router-dom";
import {useEmployee} from "@/features/employee/hooks/useEmployee.ts";
import {NotFoundEntity} from "@/components/ui/NotFoundEntity.tsx";

export const EmployeePage = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isError, isLoading } = useEmployee(id!);

    if (isLoading)
        return <div className="p-4 text-center text-zinc-500">Завантаження...</div>;

    if (isError || !data)
        return (
            <NotFoundEntity
                title="Працівника не знайдено"
                redirectTiList="/employee"
                message={`Працівник з ID ${id} не існує в базі даних`}
            />
        );

    return (
        <div className="p-4">
            <EmployeeComponent data={data} isOwnProfile={false} />
        </div>
    );
};
