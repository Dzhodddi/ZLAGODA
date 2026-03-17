import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {EditCustomerCardPage} from "@/features/customer-card/routes/EditCustomerCardPage.tsx";
import {CreateCustomerCardPage} from "@/features/customer-card/routes/CreateCustomerCardPage.tsx";
import {CustomerCardListPage} from "@/features/customer-card/routes/ListCustomerCardPage.tsx";
import {CustomerCardPage} from "@/features/customer-card/routes/CustomerCardPage.tsx";

const customerCardRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/customer-card/create" element={<CreateCustomerCardPage/>} />
            <Route path="/customer-card/edit/:id" element={<EditCustomerCardPage/>} />
            <Route path="/customer-card/:id" element={<CustomerCardPage/>} />
        </Route>
        <Route path="/customer-card" element={<CustomerCardListPage/>} />
    </>
);

export { customerCardRoutes }