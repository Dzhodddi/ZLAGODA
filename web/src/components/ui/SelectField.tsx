import { useFormContext } from "react-hook-form";

interface SelectField {
    value: string | number;
    label: string;
}

export interface SelectFieldProps {
    name: string;
    label: string;
    options: SelectField[];
    size?: number;
    required?: boolean;
    disabled?: boolean;
    valueAsNumber?: boolean;
}

export const SelectField = ({
                                name,
                                label,
                                options,
                                size = 12,
                                required = true,
                                disabled = false,
                                valueAsNumber = false,
                            }: SelectFieldProps) => {
    const { register, formState: { errors } } = useFormContext();
    const error = errors[name]?.message as string | undefined;
    const colSpanClass = `col-span-${size}`;

    return (
        <div className={`${colSpanClass} flex flex-col gap-1`}>
            <label className="text-sm font-medium text-zinc-700 flex items-center">
                {label}
                {!disabled && required && (
                    <span className="ml-1 text-red-500">*</span>
                )}
            </label>
            <select
                {...register(name, { valueAsNumber })}
                disabled={disabled}
                className={`border rounded px-1 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500
                    ${error ? "border-red-500 bg-red-50" : "border-gray-300"}
                    ${disabled ? "bg-gray-100 cursor-not-allowed shadow-md" : ""}
                `}
            >
                {options.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                        {opt.label}
                    </option>
                ))}
            </select>
            {error && <p className="text-red-500 text-xs">{error}</p>}
        </div>
    );
};
