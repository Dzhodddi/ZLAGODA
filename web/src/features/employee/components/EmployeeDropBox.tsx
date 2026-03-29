import { Controller, useFormContext } from "react-hook-form";
import {Combobox, type ComboboxOption} from "@/components/ui/ComboBox.tsx";
import {useEmployeeIDList} from "@/features/employee/hooks/useEmployee.ts";

export const EmployeeComboboxField = ({ name = "idEmployee" }: { name?: string }) => {
    const { data: employees } = useEmployeeIDList();
    const { control } = useFormContext();

    const options: ComboboxOption<string>[] | undefined = employees?.map((e) => ({
        value: e.idEmployee,
        label: "#" + e.idEmployee + " " + e.fullName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">
                        ID Працівника <span className="text-red-500">*</span>
                    </label>
                    <Combobox
                        options={options}
                        value={field.value}
                        onChange={(val) => field.onChange(val ?? undefined)}
                        placeholder="Оберіть працівника"
                        inputClassName="bg-white"
                        showAllOption={false}
                    />
                    {fieldState.error && (
                        <span className="text-xs text-red-500">{fieldState.error.message}</span>
                    )}
                </div>
            )}
        />
    );
};