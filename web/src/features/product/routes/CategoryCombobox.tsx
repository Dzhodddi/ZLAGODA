import { useState, useRef, useEffect } from "react";

type Category = {
    categoryNumber: number;
    categoryName: string;
};

type Props = {
    categories: Category[] | undefined;
    value: number | undefined;
    onChange: (id: number | undefined) => void;
};

export const CategoryCombobox = ({ categories, value, onChange }: Props) => {
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
                placeholder="Усі категорії"
                className="w-full border rounded px-3 py-1.5 text-sm text-zinc-900 bg-green-50 pr-7 cursor-pointer"
            />
            <div className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2 text-zinc-900 text-xm">
                {isOpen ? "△" : "▽"}
            </div>

            {isOpen && (
                <ul className="absolute z-50 mt-1 w-full bg-green-50 border border-zinc-200 rounded shadow-md max-h-52 overflow-y-auto text-sm">
                    <li
                        onMouseDown={() => handleSelect(null)}
                        className="px-3 py-1.5 cursor-pointer hover:bg-green-200 text-zinc-700"
                    >
                        Усі категорії
                    </li>
                    {filtered?.length === 0 && (
                        <li className="px-3 py-1.5 text-zinc-400">Нічого не знайдено</li>
                    )}
                    {filtered?.map((cat) => (
                        <li
                            key={cat.categoryNumber}
                            onMouseDown={() => handleSelect(cat)}
                            className={`px-3 py-1.5 cursor-pointer hover:bg-green-200 ${
                                cat.categoryNumber === value ? "bg-green-200 font-medium text-zinc-900" : "text-zinc-800"
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
