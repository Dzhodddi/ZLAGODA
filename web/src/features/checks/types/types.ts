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

export const CreateNewCheckSchema = z.object({
    check_number: z
        .string()
        .min(1, "Номер чеку занадто короткий")
        .max(10, "Номер чеку занадто довгий"),
    id_employee: z
        .string()
        .min(1, "ID працівника занадто короткий")
        .max(10, "ID працівника занадто довгий"),
    card_number: z
        .string()
        .min(1, "Номер картки занадто короткий")
        .max(13, "Номер картки занадто довгий"),
    print_date: z
        .iso
        .datetime("Невірний формат дати"),
    products: z
        .array(StoreProductSchema)
        .min(1, "Додайте хоча б один товар"),
    vat: z
        .coerce
        .number("Невірне число")
        .min(0, "ПДВ не може бути від'ємним")
        .max(999999999.9999, "ПДВ занадто великий"),
});

export type StoreProduct = z.infer<typeof StoreProductSchema>;
export type CreateNewCheck = z.infer<typeof CreateNewCheckSchema>;

export const smallCheckSchema = z.object({
    checkNumber: z.string()
});

export type smallCheck = z.infer<typeof smallCheckSchema>;
