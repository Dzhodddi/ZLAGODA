import { Route } from "react-router-dom";
import {EmployeeList} from "@/features/employee/components/employeeList.tsx";
import {UpsertEmployeeForm} from "@/features/employee/components/employeeForm.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {EmployeeMe} from "@/features/employee/components/employeeMe.tsx";
import {UpsertStoreProductForm} from "@/features/store_product/components/storeProductForm.tsx";
import {StoreProductList} from "@/features/store_product/components/storeProductList.tsx";
import {UpsertProductForm} from "@/features/product/components/productForm.tsx";
import {ProductList} from "@/features/product/components/productList.tsx";



const productRoutes = (
    <>
        <Route path="/product" element={<ProductList/>} />
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/product/create" element={<UpsertProductForm/>} />
            <Route path="/product/edit/:id" element={<UpsertProductForm/>} />
        </Route>
    </>
);

export { productRoutes }