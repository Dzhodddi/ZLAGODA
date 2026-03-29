import { useState, useRef, useEffect } from "react";
import { Controller, useFormContext } from "react-hook-form";
import { useAllProducts } from "@/features/product/hooks/useProduct.ts";
import { useAllCategories } from "@/features/category/hooks/useCategory.ts";

export type ComboboxOption<TValue extends string | number = number> = {
    value: TValue;
    label: string;
};

type ComboboxProps<TValue extends string | number> = {
    options: ComboboxOption<TValue>[] | undefined;
    value: TValue | undefined;
    onChange: (value: TValue | undefined) => void;
    placeholder?: string;
    inputClassName?: string;
    showAllOption?: boolean;
};

export const Combobox = <TValue extends string | number>({
                                                             options,
                                                             value,
                                                             onChange,
                                                             placeholder = "Оберіть значення",
                                                             inputClassName = "bg-green-50",
                                                             showAllOption = true,
                                                         }: ComboboxProps<TValue>) => {
    const [inputValue, setInputValue] = useState("");
    const [isOpen, setIsOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    const selectedOption = options?.find((o) => o.value === value);

    useEffect(() => {
        if (!isOpen) {
            setInputValue(selectedOption?.label ?? "");
        }
    }, [selectedOption, isOpen]);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (!containerRef.current?.contains(e.target as Node)) {
                setIsOpen(false);
                setInputValue(selectedOption?.label ?? "");
            }
        };
        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, [selectedOption]);

    const filtered = inputValue
        ? options?.filter((o) =>
            o.label.toLowerCase().includes(inputValue.toLowerCase())
        )
        : options;

    const handleSelect = (opt: ComboboxOption<TValue> | null) => {
        onChange(opt?.value ?? undefined);
        setInputValue(opt?.label ?? "");
        setIsOpen(false);
    };

    return (
        <div ref={containerRef} className="relative flex-1">
            <input
                value={inputValue}
                onChange={(e) => {
                    setInputValue(e.target.value);
                    setIsOpen(true);
                    if (!e.target.value) onChange(undefined);
                }}
                onFocus={() => setIsOpen(true)}
                placeholder={placeholder}
                className={`w-full border rounded px-3 py-1.5 text-sm text-zinc-900 pr-7 cursor-pointer ${inputClassName}`}
            />
            <div className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2 text-zinc-900 text-xm">
                {isOpen ? "△" : "▽"}
            </div>

            {isOpen && (
                <ul className="absolute z-50 mt-1 w-full bg-white border border-zinc-200 rounded shadow-md max-h-52 overflow-y-auto text-sm">
                    {showAllOption && (
                        <li
                            onMouseDown={() => handleSelect(null)}
                            className="px-3 py-1.5 cursor-pointer hover:bg-zinc-100 text-zinc-500"
                        >
                            {placeholder}
                        </li>
                    )}
                    {filtered?.length === 0 && (
                        <li className="px-3 py-1.5 text-zinc-400">Нічого не знайдено</li>
                    )}
                    {filtered?.map((opt) => (
                        <li
                            key={opt.value}
                            onMouseDown={() => handleSelect(opt)}
                            className={`px-3 py-1.5 cursor-pointer hover:bg-zinc-100 ${
                                opt.value === value
                                    ? "bg-zinc-100 font-medium text-zinc-900"
                                    : "text-zinc-800"
                            }`}
                        >
                            {opt.label}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export const CategoryComboboxField = ({ name = "categoryNumber" }: { name?: string }) => {
    const { data: categories } = useAllCategories();
    const { control } = useFormContext();

    const options: ComboboxOption[] | undefined = categories?.map((c) => ({
        value: c.categoryNumber,
        label: "#" + c.categoryNumber.toString() + " " + c.categoryName,
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

export const ProductComboboxField = ({ name = "idProduct" }: { name?: string }) => {
    const { data: products } = useAllProducts();
    const { control } = useFormContext();

    const options: ComboboxOption<number>[] | undefined = products?.map((p) => ({
        value: p.idProduct,
        label: p.productName,
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