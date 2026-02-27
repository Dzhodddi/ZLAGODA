import { useRef } from "react";
import { Form } from "@/components/ui/Form.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";
import {type Login, LoginSchema} from "@/features/auth/types/types.ts";
import {useLogin} from "@/features/auth/hooks/useAuth.ts";

export const LoginForm = () => {
    const resetFormRef = useRef<() => void>(null);

    const mutation = useLogin();

    const handleSubmit = (data: Login) => {
        mutation.mutate(data, {
            onSuccess: () => {
                if (resetFormRef.current) {
                    resetFormRef.current();
                }
            }
        });
    };

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Login</h2>

            <Form<Login>
                schema={LoginSchema}
                onSubmit={handleSubmit}
                className="grid grid-cols-12 gap-4"
            >
                {({ formState: { isSubmitting }, reset }) => {
                    resetFormRef.current = reset
                    return (
                        <>
                            <InputField name="idEmployee" label="Employee ID" />
                            <InputField name="password" label="Password" type="password"/>

                            <div className="col-span-12 flex justify-end mt-4">
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                                >
                                    {isSubmitting ? 'Saving...' : 'Login'}
                                </button>
                            </div>
                        </>
                    )
                }}
            </Form>
        </div>
    );
};