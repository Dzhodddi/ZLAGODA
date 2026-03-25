import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {CheckPage} from "@/features/checks/routes/CheckPage.tsx";
import {CheckListPage} from "@/features/checks/routes/CheckListPage.tsx";
import {CreateCheckPage} from "@/features/checks/routes/CreateCheckPage.tsx";



const checkRoutes = (
    <>
        <Route path="/check" element={<CheckListPage />} />
        <Route path="/check/:id" element={<CheckPage />} />
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/check/create" element={<CreateCheckPage />} />
        </Route>
    </>
);

export { checkRoutes }