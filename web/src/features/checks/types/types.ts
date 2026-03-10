import { z } from "zod";

export const StoreProductSchema = z.object({
    upc: z
        .string()
        .min(1, "UPC too short")
        .max(12, "UPC too long"),
    quantity: z
        .coerce
        .number("Invalid number" )
        .int("Must be an integer")
        .gte(1, "Quantity too low")
        .lt(1_000_000, "Quantity too large")
});

export const CreateNewCheckSchema = z.object({
    check_number: z
        .string()
        .min(1, "Check number too short")
        .max(10, "Check number too long"),
    id_employee: z
        .string()
        .min(1, "Employee ID too short")
        .max(10, "Employee ID too long"),
    card_number: z
        .string()
        .min(1, "Card number too short")
        .max(13, "Card number too long"),
    print_date: z
        .iso
        .datetime("Invalid date format"),
    products: z
        .array(StoreProductSchema)
        .min(1, "Add at least one product"),
    vat: z
        .coerce
        .number("Invalid number" )
        .min(0, "VAT cannot be negative")
        .max(999999999.9999, "VAT too high"),
});

export type StoreProduct = z.infer<typeof StoreProductSchema>;
export type CreateNewCheck = z.infer<typeof CreateNewCheckSchema>;