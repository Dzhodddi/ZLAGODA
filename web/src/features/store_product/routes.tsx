import { Route } from "react-router-dom";
import {EmployeeList} from "@/features/employee/components/employeeList.tsx";
import {UpsertEmployeeForm} from "@/features/employee/components/employeeForm.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {EmployeeMe} from "@/features/employee/components/employeeMe.tsx";
import {UpsertStoreProductForm} from "@/features/store_product/components/storeProductForm.tsx";
import {StoreProductList} from "@/features/store_product/components/storeProductList.tsx";



const storeProductRoutes = (
    <>
        <Route path="/store-product" element={<StoreProductList/>} />
        <Route path="/store-product/create" element={<UpsertStoreProductForm/>} />
        <Route path="/store-product/edit/:upc" element={<UpsertStoreProductForm/>} />
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/employee/me" element={<EmployeeMe />} />
        </Route>
    </>
);

export { storeProductRoutes }