import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { getEmployee } from "@/features/employee/api/employeeApi.ts";
import { UpsertEmployeeForm } from "@/features/employee/components/employeeForm.tsx";

export const EditEmployeePage = () => {
    const { id } = useParams<{ id: string }>();

    const { data, isLoading } = useQuery({
        queryKey: ['employees', id],
        queryFn: () => getEmployee(id!),
        enabled: !!id,
    });

    if (isLoading)
        return <div className="p-4 text-center">Завантаження...</div>;

    return (
        <div className="p-4">
            <UpsertEmployeeForm initialData={data} />
        </div>
    );
};