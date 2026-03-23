import {z} from 'zod';

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
    productName: z
        .string()
        .min(1, "Назва занадто коротка")
        .max(50, "Назва занадто довга")
        .nullish(),
    sellingPrice: z
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

export const PageStoreProductSchema = z.object({
    content: z.array(BaseStoreProductSchema),
    pageSize: z.number(),
    totalElements: z.number(),
    hasNext: z.boolean(),
});

export type StoreProduct = z.infer<typeof BaseStoreProductSchema>;

export const CreateStoreProductSchema = BaseStoreProductSchema.refine(
    data => !data.promotionalProduct || data.upcProm !== null,
    { message: "Акційний товар повинен мати UPC промо", path: ["upcProm"] }
);

export type CreateStoreProduct = z.infer<typeof CreateStoreProductSchema>;

export const StoreProductPriceAndQuantitySchema = z.object({
    sellingPrice: z.number(),
    productsNumber: z.number(),
});

export type StoreProductPriceAndQuantity = z.infer<typeof StoreProductPriceAndQuantitySchema>;

export const BatchRequestSchema = z.object({
    UPC: z
        .string()
        .min(1, "UPC обов'язковий")
        .max(12, "UPC занадто довгий"),
    delivery_date: z
        .iso
        .date("Неправильний формат дати"),
    expiring_date: z
        .iso
        .date("Неправильний формат дати"),
    quantity: z
        .coerce
        .number()
        .int()
        .min(1, "Кількість має бути не менше 1"),
    price: z
        .coerce
        .number()
        .min(0.01, "Ціна має бути більше 0"),
});

export type BatchRequest = z.infer<typeof BatchRequestSchema>;

export const BatchRequestFormSchema
    = BatchRequestSchema.refine(
    data => data.expiring_date > data.delivery_date,
    { message: "Дата закінчення має бути пізніше за дату доставки", path: ["expiring_date"] }
);

export const BatchDtoSchema = z.object({
    id: z.number(),
    UPC: z.string(),
    delivery_date: z.string(),
    expiring_date: z.string(),
    quantity: z.number().int(),
    selling_price: z.number(),
});

export type BatchDto = z.infer<typeof BatchDtoSchema>;