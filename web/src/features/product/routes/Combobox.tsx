import { useState, useRef, useEffect } from "react";
import { useAllProducts } from "@/features/product/hooks/useProduct.ts";
import { Controller, useFormContext } from "react-hook-form";
import { useAllCategories } from "@/features/category/hooks/useCategory.ts";

type Category = {
    categoryNumber: number;
    categoryName: string;
};

type Props = {
    categories: Category[] | undefined;
    value: number | undefined;
    onChange: (id: number | undefined) => void;
    placeholder?: string;
    inputClassName?: string;
    showAllOption?: boolean;
};

export const Combobox = ({
                                     categories,
                                     value,
                                     onChange,
                                     placeholder = "Усі категорії",
                                     inputClassName = "bg-green-50",
                                     showAllOption = true,
                                 }: Props) => {
    const [inputValue, setInputValue] = useState("");
    const [isOpen, setIsOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    const selectedCategory = categories?.find(c => c.categoryNumber === value);

    useEffect(() => {
        if (!isOpen) {
            setInputValue(selectedCategory?.categoryName ?? "");
        }
    }, [selectedCategory, isOpen]);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (!containerRef.current?.contains(e.target as Node)) {
                setIsOpen(false);
                setInputValue(selectedCategory?.categoryName ?? "");
            }
        };
        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, [selectedCategory]);

    const filtered = inputValue
        ? categories?.filter(c =>
            c.categoryName.toLowerCase().includes(inputValue.toLowerCase())
        )
        : categories;

    const handleSelect = (cat: Category | null) => {
        onChange(cat?.categoryNumber ?? undefined);
        setInputValue(cat?.categoryName ?? "");
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
                    {filtered?.map((cat) => (
                        <li
                            key={cat.categoryNumber}
                            onMouseDown={() => handleSelect(cat)}
                            className={`px-3 py-1.5 cursor-pointer hover:bg-zinc-100 ${
                                cat.categoryNumber === value ? "bg-zinc-100 font-medium text-zinc-900" : "text-zinc-800"
                            }`}
                        >
                            {cat.categoryName}
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

    const items = categories?.map(c => ({
        categoryNumber: c.categoryNumber,
        categoryName: c.categoryName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">Категорія <span className="text-red-500">*</span></label>
                    <Combobox
                        categories={items}
                        value={field.value}
                        onChange={(id) => field.onChange(id ?? null)}
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

    const items = products?.map(p => ({
        categoryNumber: p.idProduct,
        categoryName: p.productName,
    }));

    return (
        <Controller
            control={control}
            name={name}
            render={({ field, fieldState }) => (
                <div className="col-span-12 flex flex-col gap-1 py-1.5">
                    <label className="text-sm font-medium text-zinc-700">Товар <span className="text-red-500">*</span></label>
                    <Combobox
                        categories={items}
                        value={field.value}
                        onChange={(id) => field.onChange(id ?? null)}
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
