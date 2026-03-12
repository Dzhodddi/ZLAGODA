import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
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