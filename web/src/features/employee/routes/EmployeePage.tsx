import {EmployeeComponent} from "@/features/employee/components/EmployeeComponent.tsx";
import {useParams} from "react-router-dom";
import {useEmployee} from "@/features/employee/hooks/useEmployee.ts";

export const EmployeePage = () => {
    const { id } = useParams<{ id: string }>();
    const { data, isLoading } = useEmployee(id!);

    if (isLoading) return <p>Завантаження...</p>;
    if (!data) return <p>Немає даних</p>;
    return (
        <div className="p-4">
            <EmployeeComponent data={data} isOwnProfile={false} />
        </div>
    );
};