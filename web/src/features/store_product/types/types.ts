import {z} from 'zod';

export const StoreProductSchema = z.object({
    upc: z.string(),
    upcProm: z.string().nullable().optional(),
    idProduct: z.number(),
    sellingPrice: z.coerce.number(),
    productsNumber: z.number(),
    promotionalProduct: z.boolean(),
});

export type StoreProduct = z.infer<typeof StoreProductSchema>;

export const PageStoreProductSchema = z.object({
    content: z.array(StoreProductSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export const BaseStoreProductSchema = z.object({
    upc: z
        .string()
        .min(1, "UPC занадто короткий")
        .max(12, "UPC занадто довгий"),
    upcProm: z
        .string()
        .max(12, "UPC занадто довгий")
        .optional()
        .nullable()
        .transform(val => val?.trim() === "" ? null : val),
    idProduct: z
        .coerce
        .number()
        .int()
        .min(1, "ID товару має бути позитивним"),
    price: z
        .coerce
        .number()
        .min(0, "Ціна має бути позитивною")
        .max(999999999.9999, "Ціна завелика"),
    productsNumber: z
        .coerce
        .number()
        .int()
        .min(1, "Кількість має бути позитивною")
        .max(999999999, "Занадто багато товарів"),
    promotionalProduct: z
        .boolean()
        .default(false),
});

export const CreateStoreProductSchema = BaseStoreProductSchema.refine(
    data => !data.promotionalProduct || data.upcProm !== null,
    { message: "Акційний товар повинен мати UPC промо", path: ["upcProm"] }
);

export type CreateStoreProduct = z.infer<typeof CreateStoreProductSchema>;

export const StoreProductPriceAndQuantitySchema = z.object({
    selling_price: z.number(),
    products_number: z.number(),
});

export type StoreProductPriceAndQuantity = z.infer<typeof StoreProductPriceAndQuantitySchema>;

export const PageStoreProductPriceAndQuantitySchema = z.object({
    content: z.array(StoreProductPriceAndQuantitySchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});
