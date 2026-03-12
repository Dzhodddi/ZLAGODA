import { useRef } from "react";
import { type FieldValues, type UseFormReturn } from "react-hook-form";
import { type ZodType } from "zod";
import { Form } from "@/components/ui/Form.tsx";

interface GenericUpsertFormProps<TFormValues extends FieldValues, TUpdatePayload, TInitialData = TFormValues> {
    schema: ZodType<TFormValues, any, any>;
    initialData?: TInitialData | null;

    createMutation: { mutate: (data: TFormValues, options?: any) => void; isPending: boolean };
    updateMutation: { mutate: (data: TUpdatePayload, options?: any) => void; isPending: boolean };

    prepareUpdatePayload: (formData: TFormValues, initialData: TInitialData) => TUpdatePayload; // ✅

    onSuccessAction?: () => void;
    className?: string;

    children: (
        methods: UseFormReturn<TFormValues>,
        context: { isEditMode: boolean; isSaving: boolean, isDirty: boolean }
    ) => React.ReactNode;
}

export const GenericUpsertForm = <TFormValues extends FieldValues, TUpdatePayload, TInitialData = TFormValues>({
    schema,
    initialData,
    createMutation,
    updateMutation,
    prepareUpdatePayload,
    onSuccessAction,
    className,
    children
}: GenericUpsertFormProps<TFormValues, TUpdatePayload, TInitialData>) => {

    const resetFormRef = useRef<() => void>(null);
    const isEditMode = !!initialData;
    const isSaving = createMutation.isPending || updateMutation.isPending;

    const handleSubmit = (data: TFormValues) => {
        const handleSuccess = () => {
            resetFormRef.current?.();
            onSuccessAction?.();
        };

        if (isEditMode && initialData) {
            const payload = prepareUpdatePayload(data, initialData as TInitialData);
            updateMutation.mutate(payload, { onSuccess: handleSuccess });
        } else {
            createMutation.mutate(data, { onSuccess: handleSuccess });
        }
    };

    return (
        <Form<TFormValues>
            schema={schema}
            onSubmit={handleSubmit}
            values={initialData as unknown as TFormValues || undefined} // ✅
            className={className}
        >
            {(methods) => {
                resetFormRef.current = methods.reset;
                const isDirty = methods.formState.isDirty;
                return children(methods, { isEditMode, isSaving, isDirty });
            }}
        </Form>
    );
};