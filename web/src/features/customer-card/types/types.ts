import {z} from 'zod';

const phoneRegex = new RegExp(
    /^([+]?[\s0-9]+)?(\d{3}|[(]?[0-9]+[)])?([-]?[\s]?[0-9])+$/
);


export const CreateCustomerCard = z.object({
    CardNumber: z.string().min(1).max(13),
    CustomerName: z.string().min(1).max(50),
    CustomerSurname: z.string().min(1).max(50),
    CustomerPatronymic: z.optional(z.string().min(1).max(50)),
    PhoneNumber: z.string().min(1).max(13).regex(phoneRegex, 'Invalid phone number'),
    City: z.optional(z.string().min(1).max(50)),
    Street: z.optional(z.string().min(1).max(50)),
    ZipCode: z.optional(z.string().min(1).max(9)),
    CustomerPercent: z.number().min(0).max(100),
});

export type CreateCustomerCard = z.infer<typeof CreateCustomerCard>;
