import { z } from "zod";

export const StoreProductSchema = z.object({
    upc: z
        .string()
        .min(1, "UPC занадто короткий")
        .max(12, "UPC занадто довгий"),
    quantity: z
        .coerce
        .number("Невірне число")
        .int("Має бути цілим числом")
        .gte(1, "Кількість занадто мала")
        .lt(1_000_000, "Кількість занадто велика")
});

export const CheckSchema = z.object({
    checkNumber: z
        .string()
        .min(1, "Номер чеку занадто короткий")
        .max(10, "Номер чеку занадто довгий"),
    idEmployee: z
        .string("Оберіть працівника")
        .min(1, "ID працівника занадто короткий")
        .max(10, "ID працівника занадто довгий"),
    cardNumber: z
        .string("Оберіть картку клієнта")
        .min(1, "Номер картки занадто короткий")
        .max(13, "Номер картки занадто довгий"),
    printDate: z
        .string()
        .min(1, "Вкажіть дату друку")
        .refine((val) => !isNaN(Date.parse(val)), {
            message: "Невірний формат дати",
        })
        .transform((val) => new Date(val).toISOString()),
    products: z
        .array(StoreProductSchema)
        .min(1, "Додайте хоча б один товар"),
    vat: z
        .coerce
        .number("Невірне число")
        .min(0, "ПДВ не може бути від'ємним")
        .max(999999999.9999, "ПДВ занадто великий"),
});

export const CreateCheckSchema = CheckSchema.omit({vat: true})

export const CheckListItemSchema = CheckSchema.omit({ products: true }).extend(
    {
        sumTotal: z.number(),
    }
);

const ProductInListSchema = z.object({
    name: z.string().min(1, "Назва обов'язкова"),
    quantity: z.coerce.number().gte(1, "Кількість занадто мала"),
    sellingPrice: z.coerce.number().min(0, "Ціна не може бути від'ємною")
});

const CheckDetailsInListSchema = z.object({
    checkNumber: z.string().min(1, "Номер чеку обов'язковий"),
    idEmployee: z.string().min(1, "ID працівника обов'язковий"),
    cardNumber: z.string().min(1, "Номер картки обов'язковий"),
    printDate: z.string(),
    sumTotal: z.coerce.number().min(0),
    vat: z.coerce.number().min(0, "ПДВ не може бути від'ємним")
});

export const CheckItemSchema = z.object({
    check: CheckDetailsInListSchema,
    products: z.array(ProductInListSchema)
});

export type StoreProduct = z.infer<typeof StoreProductSchema>;
export type Check = z.infer<typeof CheckSchema>;
export type CheckItem = z.infer<typeof CheckItemSchema>;
export type CheckListItem = z.infer<typeof CheckListItemSchema>;