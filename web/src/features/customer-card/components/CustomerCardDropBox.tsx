import { Controller, useFormContext } from "react-hook-form";
import {Combobox, type ComboboxOption} from "@/components/ui/ComboBox.tsx";
import {useCustomerCardIDList} from "@/features/customer-card/hooks/useCustomerCard.ts";

export const CustomerCardComboboxField = ({ name = "cardNumber" }: { name?: string }) => {
    const { data: cards } = useCustomerCardIDList();
    const { control } = useFormContext();

    const options: ComboboxOption<string>[] | undefined = cards?.map((c) => ({
        value: c.cardNumber,
        label: "#" + c.cardNumber + " " + c.fullName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">
                        Номер картки клієнта <span className="text-red-500">*</span>
                    </label>
                    <Combobox
                        options={options}
                        value={field.value}
                        onChange={(val) => field.onChange(val ?? undefined)}
                        placeholder="Оберіть картку клієнта"
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