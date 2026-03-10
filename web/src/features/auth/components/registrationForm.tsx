import { useRef } from "react";
import { Form } from "@/components/ui/Form.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";
import { type RegisterEmployee, RegisterEmployeeSchema } from "@/features/auth/types/types.ts";
import { useRegister } from "@/features/auth/hooks/useAuth.ts";
import { Link } from "react-router-dom";
import {useFormContext} from "react-hook-form";

const RoleSelect = () => {
    const { register } = useFormContext();
    return (
        <div className="col-span-12 flex flex-col gap-1">
            <label className="text-sm font-medium text-zinc-700">Роль</label>
            <select
                {...register("role")}
                className="border rounded px-1 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
                <option value="">Оберіть роль</option>
                <option value="MANAGER">Менеджер</option>
                <option value="CASHIER">Касир</option>
            </select>
        </div>
    );
};

export const RegistrationForm = () => {
    const resetFormRef = useRef<() => void>(null);
    const mutation = useRegister();

    const handleSubmit = (data: RegisterEmployee) => {
        mutation.mutate(data, {
            onSuccess: () => resetFormRef.current?.(),
        });
    };

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto max-h-[100vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">Зареєструватися</h2>

            <Form<RegisterEmployee>
                schema={RegisterEmployeeSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting }, reset }) => {
                    resetFormRef.current = reset;
                    return (
                        <>
                            <InputField name="idEmployee" label="ID працівника" />
                            <InputField name="emplSurname" label="Прізвище" />
                            <InputField name="emplName" label="Ім'я" />
                            <InputField name="emplPatronymic" label="По батькові" />
                            <RoleSelect />
                            <InputField type="number" name="salary" label="Зарплата" />
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
                                    disabled={isSubmitting}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSubmitting ? "Збереження..." : "Зареєструватися"}
                                </button>
                            </div>
                        </>
                    );
                }}
            </Form>
        </div>
    );
};
