import { Route } from "react-router-dom";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";

const customerCardRoutes = (
    <>
        <Route path="/card" element={<UpsertCustomerCardForm/>} />
    </>
);

export { customerCardRoutes }