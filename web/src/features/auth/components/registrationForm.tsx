import { InputField } from "@/components/ui/InputFields.tsx";import { useRegister } from "@/features/auth/hooks/useAuth.ts";
import {Link, useNavigate} from "react-router-dom";
import {useFormContext} from "react-hook-form";
import {type CreateEmployee, CreateEmployeeSchema, type Employee} from "@/features/employee/types/types.ts";
import {GenericUpsertForm} from "@/components/ui/GenericUpsertForm.tsx";

const RoleSelect = () => {
    const { register } = useFormContext();
    return (
        <div className="col-span-12 flex flex-col gap-1">
            <label className="text-sm font-medium text-zinc-700">Роль</label>
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

export const RegistrationForm = () => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto max-h-screen overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">Зареєструватися</h2>

            <GenericUpsertForm<CreateEmployee, CreateEmployee, Employee>
                schema={CreateEmployeeSchema}
                initialData={undefined}
                createMutation={useRegister()}
                updateMutation={useRegister()}
                prepareUpdatePayload={(formData, initial) => ({
                    ...formData,
                    idEmployee: initial.idEmployee
                })}
                onSuccessAction={() => navigate("/")}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isEditMode, isSaving, isDirty }) => {
                    return (
                        <>
                            <InputField name="idEmployee" label="ID працівника" />
                            <InputField name="emplSurname" label="Прізвище" />
                            <InputField name="emplName" label="Ім'я" />
                            <InputField name="emplPatronymic" label="По батькові" required={false}/>
                            <RoleSelect />
                            <InputField name="salary" label="Зарплата" />
                            <InputField type="date" name="dateOfBirth" label="Дата народження" />
                            <InputField type="date" name="dateOfStart" label="Дата прийому на роботу" />
                            <InputField name="phoneNumber" label="Номер телефону" />
                            <InputField name="city" label="Місто" />
                            <InputField name="street" label="Вулиця" />
                            <InputField name="zipCode" label="Поштовий індекс" />
                            <InputField type="password" name="password" label="Пароль" />
                            <InputField type="password" name="repeatPassword" label="Повторіть пароль" />

                            <div className="col-span-12 flex justify-between items-center mt-4">
                                <Link to="/login" className="text-blue-600 hover:underline text-sm">
                                    Маєте акаунт? Увійти
                                </Link>
                                <button
                                    type="submit"
                                    disabled={isSaving || (isEditMode && !isDirty)}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSaving ? "Збереження..." : "Зареєструватися"}
                                </button>
                            </div>
                        </>
                    );
                }}
            </GenericUpsertForm>
        </div>
    );
};
