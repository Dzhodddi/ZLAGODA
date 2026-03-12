import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {UpsertStoreProductForm} from "@/features/store_product/components/storeProductForm.tsx";
import {StoreProductList} from "@/features/store_product/components/storeProductList.tsx";
import {OwnEmployeePage} from "@/features/employee/routes/EmployeeMePage.tsx";

const storeProductRoutes = (
    <>
        <Route path="/store-product" element={<StoreProductList/>} />
        <Route path="/store-product/create" element={<UpsertStoreProductForm/>} />
        <Route path="/store-product/edit/:upc" element={<UpsertStoreProductForm/>} />
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/employee/me" element={<OwnEmployeePage />} />
        </Route>
    </>
);

export { storeProductRoutes }