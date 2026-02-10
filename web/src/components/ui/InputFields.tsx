import { useFormContext } from 'react-hook-form';
import * as React from "react";

interface InputFieldProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'> {
    name: string;
    label: string;
    size?: number;
}

export const InputField = ({
                               name,
                               label,
                               size = 12,
                               className,
                               ...props
                           }: InputFieldProps) => {
    const { register, formState: { errors } } = useFormContext();

    const error = errors[name]?.message as string | undefined;
    const colSpanClass = `col-span-${size}`
    return (
        <div className={`${colSpanClass} flex flex-col gap-1`}>
            <label htmlFor={name} className="text-sm font-medium text-gray-700">
                {label}
            </label>

            <input
                id={name}
                {...register(name)}
                {...props}
                className={`
          border rounded p-2 focus:ring-2 focus:ring-blue-500 outline-none text-zinc-900
          ${error ? 'border-red-500 bg-red-50' : 'border-gray-300'}
          ${className}
            `}
            />

            {error && <span className="text-xs text-red-500">{error}</span>}
        </div>
    );
};