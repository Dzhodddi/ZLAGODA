import { useNavigate, useParams } from "react-router-dom";
import {
    type BatchRequest,
    BatchRequestFormSchema,
} from "@/features/store_product/types/types.ts";
import { useReceiveNewBatch } from "@/features/store_product/hooks/useStoreProduct.ts";
import { GenericUpsertForm } from "@/components/ui/GenericUpsertForm.tsx";
import { InputField } from "@/components/ui/InputFields.tsx";

export const UpsertBatchForm = () => {
    const { upc } = useParams<{ upc: string }>();
    const navigate = useNavigate();
    const receiveBatchMutation = useReceiveNewBatch();

    const defaultValues: BatchRequest = {
        UPC: upc ?? "",
        delivery_date: "",
        expiring_date: "",
        quantity: 1,
        price: 0,
    };

    return (
        <div className="p-6 bg-white rounded text-zinc-900 shadow-md max-w-2xl mx-auto">
            <GenericUpsertForm<BatchRequest, BatchRequest, BatchRequest>
                schema={BatchRequestFormSchema}
                initialData={defaultValues}
                createMutation={receiveBatchMutation}
                onSuccessAction={() => {
                    navigate("/store-product");
                }}
                className="grid grid-cols-12 gap-4"
            >
                {(_methods, { isSaving }) => (
                    <>
                        <h2 className="col-span-12 text-xl font-bold mb-4">
                            Отримати нову партію товару у магазин
                        </h2>

                        <InputField name="UPC" label="UPC" disabled={!!upc} />
                        <InputField type="date" name="delivery_date" label="Дата доставки" />
                        <InputField type="date" name="expiring_date" label="Термін придатності" />
                        <InputField type="number" name="quantity" label="Кількість одиниць" min="1" />
                        <InputField type="number" name="price" label="Базова ціна, грн" min="0.01" step="0.01" />

                        <div className="col-span-12 flex justify-end mt-4">
                            <button
                                type="submit"
                                disabled={isSaving}
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                            >
                                {isSaving ? "Збереження..." : "Підтвердити"}
                            </button>
                        </div>
                    </>
                )}
            </GenericUpsertForm>
        </div>
    );
};
