import { z } from 'zod';

export const SaleSchema = z.object({
  checkNumber: z.string(),
  productNumber: z.number(),
  sellingPrice: z.number(),
  upc: z.string(),
});

export type Sale = z.infer<typeof SaleSchema>;