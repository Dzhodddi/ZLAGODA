import { useState } from "react";
import { InputField } from "@/components/ui/InputFields.tsx";
import { useRegister } from "@/features/auth/hooks/useAuth.ts";
import { Link, useNavigate } from "react-router-dom";
import { useFormContext } from "react-hook-form";
import {
    type CreateEmployee,
    CreateEmployeeSchema,
    type Employee,
} from "@/features/employee/types/types.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";

const STEPS = [
    { label: "Особисті дані" },
    { label: "Робота і контакти" },
    { label: "Адреса" },
    { label: "Безпека" },
] as const;

const STEP_FIELDS: Record<number, (keyof CreateEmployee)[]> = {
    0: ["idEmployee", "emplSurname", "emplName", "emplPatronymic", "dateOfBirth"],
    1: ["role", "salary", "dateOfStart", "phoneNumber"],
    2: ["city", "street", "zipCode"],
    3: ["password", "repeatPassword"],
};

const RoleSelect = () => {
    const { register } = useFormContext();
    return (
        <div className="col-span-12 flex flex-col gap-1">
            <label className="text-sm font-medium text-zinc-700">Посада</label>
            <select
                {...register("role")}
                className="border rounded px-1 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
            >
                <option value="MANAGER">Менеджер</option>
                <option value="CASHIER">Касир</option>
            </select>
        </div>
    );
};

interface StepIndicatorProps {
    current: number;
}

const StepIndicator = ({ current }: StepIndicatorProps) => (
    <div className="w-full mb-8">
        <div
            className="grid"
            style={{ gridTemplateColumns: `repeat(${STEPS.length}, 1fr)` }}
        >
            {STEPS.map((_, idx) => (
                <div key={idx} className="flex items-center">
                    <div className={[
                        "flex-1 h-[2px] transition-colors duration-300",
                        idx === 0 ? "invisible" : idx <= current ? "bg-blue-600" : "bg-zinc-200",
                    ].join(" ")} />

                    <div className={[
                        "w-4 h-4 rounded-full shrink-0 transition-all duration-200",
                        idx < current   ? "bg-blue-600"                      : "",
                        idx === current ? "bg-blue-600 ring-4 ring-blue-100" : "",
                        idx > current   ? "bg-zinc-200"                      : "",
                    ].join(" ")} />

                    <div className={[
                        "flex-1 h-[2px] transition-colors duration-300",
                        idx === STEPS.length - 1 ? "invisible" : idx < current ? "bg-blue-600" : "bg-zinc-200",
                    ].join(" ")} />
                </div>
            ))}

            {STEPS.map((step, idx) => (
                <div key={idx} className="flex justify-center mt-2">
                    <span className={[
                        "text-xs text-center",
                        idx === current ? "text-blue-600 font-medium" : "text-zinc-400",
                    ].join(" ")}>
                        {step.label}
                    </span>
                </div>
            ))}
        </div>
    </div>
);

export const RegistrationForm = () => {
    const navigate = useNavigate();
    const [step, setStep] = useState(0);

    return (
        <div className="p-6 bg-white rounded-xl text-zinc-900 shadow-lg max-w-2xl mx-auto max-h-screen overflow-y-auto">
            <h2 className="text-xl font-bold mb-6">Створення облікового запису</h2>

            <GenericUpsertForm<CreateEmployee, CreateEmployee, Employee>
                schema={CreateEmployeeSchema}
                initialData={undefined}
                createMutation={useRegister()}
                updateMutation={useRegister()}
                prepareUpdatePayload={(formData,
                                       initial) => ({
                    ...formData,
                    idEmployee: initial.idEmployee,
                })}
                onSuccessAction={() => navigate("/")}
                className="flex flex-col gap-4"
            >
                {(methods, { isEditMode, isSaving, isDirty }) => {
                    const { trigger } = methods;

                    const handleNext = async () => {
                        const valid = await trigger(STEP_FIELDS[step] as any);
                        if (valid) setStep((s) => Math.min(s + 1, STEPS.length - 1));
                    };

                    const handleBack = () => setStep((s) => Math.max(s - 1, 0));

                    return (
                        <>
                            <StepIndicator current={step} />

                            <div className="grid grid-cols-12 gap-4">

                                <div className={`contents ${step !== 0 ? "hidden" : ""}`}>
                                    <InputField name="idEmployee" label="ID працівника" />
                                    <InputField name="emplSurname" label="Прізвище" />
                                    <InputField name="emplName" label="Ім'я" />
                                    <InputField name="emplPatronymic" label="По батькові" required={false} />
                                    <InputField type="date" name="dateOfBirth" label="Дата народження" />
                                </div>

                                <div className={`contents ${step !== 1 ? "hidden" : ""}`}>
                                    <RoleSelect />
                                    <InputField name="salary" label="Зарплата" />
                                    <InputField type="date" name="dateOfStart" label="Дата прийому на роботу" />
                                    <InputField name="phoneNumber" label="Номер телефону" />
                                </div>

                                <div className={`contents ${step !== 2 ? "hidden" : ""}`}>
                                    <InputField name="city" label="Місто" />
                                    <InputField name="street" label="Вулиця" />
                                    <InputField name="zipCode" label="Поштовий індекс" />
                                </div>

                                <div className={`contents ${step !== 3 ? "hidden" : ""}`}>
                                    <InputField type="password" name="password" label="Пароль" />
                                    <InputField type="password" name="repeatPassword" label="Повторіть пароль" />
                                </div>

                            </div>

                            <div className="flex justify-between items-center mt-6 pt-4 border-t border-zinc-100">
                                {step === 0 ? (
                                    <Link to="/login" className="text-blue-600 hover:underline text-sm">
                                        Маєте акаунт? Увійти
                                    </Link>
                                ) : (
                                    <button
                                        type="button"
                                        onClick={handleBack}
                                        className="flex items-center gap-1 text-sm text-zinc-600 hover:text-zinc-900 transition"
                                    >
                                        Назад
                                    </button>
                                )}

                                {step < STEPS.length - 1 ? (
                                    <button
                                        type="button"
                                        onClick={handleNext}
                                        className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 disabled:opacity-50"
                                    >
                                        Далі
                                    </button>
                                ) : (
                                    <button
                                        type="submit"
                                        disabled={isSaving || (isEditMode && !isDirty)}
                                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                    >
                                        {isSaving ? "Збереження..." : "Зареєструватися"}
                                    </button>
                                )}
                            </div>
                        </>
                    );
                }}
            </GenericUpsertForm>
        </div>
    );
};