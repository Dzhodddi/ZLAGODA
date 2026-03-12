import {useGetMe} from "@/features/employee/hooks/useEmployee.ts";
import {EmployeeComponent} from "@/features/employee/components/EmployeeComponent.tsx";

export const OwnEmployeePage = () => {
    const { data, isLoading } = useGetMe();

    if (isLoading) return <p>Завантаження...</p>;
    if (!data) return <p>Немає даних</p>;
    return (
        <div className="p-4">
            <EmployeeComponent data={data} isOwnProfile={true} />
        </div>
    );
};