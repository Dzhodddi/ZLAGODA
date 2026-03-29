import { Controller, useFormContext } from "react-hook-form";
import { Combobox, type ComboboxOption } from "@/components/ui/ComboBox.tsx";
import {useStoreProductsList} from "@/features/store_product/hooks/useStoreProduct.ts";

interface Props {
    name: string;
}

export const StoreProductComboboxField = ({ name }: Props) => {
    const { data: storeProducts } = useStoreProductsList();
    const { control } = useFormContext();

    const options: ComboboxOption<string>[] | undefined = storeProducts?.map((sp) => ({
        value: sp.upc,
        label: `${sp.productName} — ${sp.sellingPrice} грн (Залишок: ${sp.quantity} шт.)`,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="flex flex-col gap-1 w-full">
                    <label className="text-sm font-medium text-zinc-700">
                        Товар (UPC) <span className="text-red-500">*</span>
                    </label>
                    <Combobox
                        options={options}
                        value={field.value}
                        onChange={(val) => field.onChange(val ?? undefined)}
                        placeholder="Оберіть товар за назвою або UPC"
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