import {
    type FieldValues,
    FormProvider,
    type SubmitHandler,
    useForm,
    type UseFormProps,
    type UseFormReturn,
} from "react-hook-form";
import type {ZodType} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import * as React from "react";

interface FormProps<T extends FieldValues> extends UseFormProps<T> {
    schema: ZodType<T, any, any>;
    onSubmit: SubmitHandler<T>;
    children: (methods: UseFormReturn<T>) => React.ReactNode;
    className?: string
}

export const Form = <T extends FieldValues>({
    schema,
    onSubmit,
    children,
    className,
    ...useFormProps
}: FormProps<T>) => {
    const methods = useForm<T>({
        resolver: zodResolver(schema),
        ...useFormProps
    })

    return (
        <FormProvider {...methods}>
            <form
                onSubmit={methods.handleSubmit(onSubmit)}
            >
                {children(methods)}
            </form>
        </FormProvider>
    )
}