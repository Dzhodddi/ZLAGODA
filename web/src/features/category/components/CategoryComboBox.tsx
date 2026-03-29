import {useAllCategories} from "@/features/category/hooks/useCategory.ts";
import {Controller, useFormContext} from "react-hook-form";
import {Combobox, type ComboboxOption} from "@/components/ui/ComboBox.tsx";

export const CategoryComboboxField = ({ name = "categoryNumber" }: { name?: string }) => {
    const { data: categories } = useAllCategories();
    const { control } = useFormContext();

    const options: ComboboxOption[] | undefined = categories?.map((c) => ({
        value: c.categoryNumber,
        label: "#" + c.categoryNumber + " " + c.categoryName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">
                        Категорія <span className="text-red-500">*</span>
                    </label>
                    <Combobox
                        options={options}
                        value={field.value}
                        onChange={(val) => field.onChange(val ?? null)}
                        placeholder="Оберіть категорію"
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