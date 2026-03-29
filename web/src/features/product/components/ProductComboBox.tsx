import { Controller, useFormContext } from "react-hook-form";
import { useAllProducts } from "@/features/product/hooks/useProduct.ts";
import {Combobox, type ComboboxOption} from "@/components/ui/ComboBox.tsx";

export const ProductComboboxField = ({ name = "idProduct" }: { name?: string }) => {
    const { data: products } = useAllProducts();
    const { control } = useFormContext();

    const options: ComboboxOption[] | undefined = products?.map((p) => ({
        value: p.idProduct,
        label: "#" + p.idProduct + " " + p.productName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">
                        Товар <span className="text-red-500">*</span>
                    </label>
                    <Combobox
                        options={options}
                        value={field.value}
                        onChange={(val) => field.onChange(val ?? null)}
                        placeholder="Оберіть товар"
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