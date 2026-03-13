import { useFormContext } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { useCreateEmployee, useUpdateEmployee, useEmployee } from "@/features/employee/hooks/useEmployee.ts";
import {
    type Employee,
    type CreateEmployee,
    CreateEmployeeSchema, EmployeeSchema
} from "@/features/employee/types/types.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";

interface Props {
    initialData?: Employee;
}

const RoleSelect = () => {
    const { register } = useFormContext();
    return (
        <div className="col-span-12 flex flex-col gap-1">
            <label className="text-sm font-medium text-zinc-700">Посада</label>
            <select
                {...register("role")}
                className="border rounded px-1 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
                <option value="MANAGER">Менеджер</option>
                <option value="CASHIER">Касир</option>
            </select>
        </div>
    );
};

export const UpsertEmployeeForm = ({ initialData }: Props) => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <GenericUpsertForm<CreateEmployee, CreateEmployee, Employee>
                schema={initialData ? EmployeeSchema : CreateEmployeeSchema}
                initialData={initialData}
                createMutation={useCreateEmployee()}
                updateMutation={useUpdateEmployee()}
                onSuccessAction={() => navigate("/employee")}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    idEmployee: initial.idEmployee
                })}
                className="grid grid-cols-12 gap-4"
            >

                {(_methods, { isEditMode, isSaving, isDirty }) => {
                    return <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            {isEditMode ? "Редагувати працівника" : "Додати працівника"}
                        </h2>
                        <InputField name="idEmployee" label="ID працівника" disabled={isEditMode}/>
                        <InputField name="emplSurname" label="Прізвище" />
                        <InputField name="emplName" label="Ім'я" />
                        <InputField name="emplPatronymic" label="По батькові" />
                        <RoleSelect />
                        <InputField type="number" name="salary" label="Зарплата, грн" min="0" step="0.01"/>
                        <InputField type="date" name="dateOfBirth" label="Дата народження" />
                        <InputField type="date" name="dateOfStart" label="Дата початку роботи" />
                        <InputField name="phoneNumber" label="Контактний телефон" />
                        <InputField name="city" label="Місто" />
                        <InputField name="street" label="Вулиця" />
                        <InputField name="zipCode" label="Поштовий індекс" />
                        {!isEditMode && (
                            <>
                                <InputField type="password" name="password" label="Пароль" />
                                <InputField type="password" name="repeatPassword" label="Повторіть пароль" />
                            </>
                        )}

                        <div className="col-span-12 flex justify-end mt-4">
                            <button
                                type="submit"
                                disabled={isSaving || (isEditMode && !isDirty)}
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                            >
                                {isSaving ? "Збереження..." : isEditMode ? "Оновити" : "Створити"}
                            </button>
                        </div>
                    </>
                }}
            </GenericUpsertForm>
        </div>
    );
};
