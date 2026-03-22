import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {ProductList} from "@/features/product/routes/ProductList.tsx";
import {SoldProductsPage} from "@/features/product/routes/SoldProductsPage.tsx";
import {ProductPage} from "@/features/product/routes/ProductPage.tsx";
import {CreateProductPage} from "@/features/product/routes/CreateProductPage.tsx";
import {EditProductPage} from "@/features/product/routes/EditProductPage.tsx";

const productRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER", "CASHIER"]} />}>
            <Route path="/product" element={<ProductList/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/product/sold" element={<SoldProductsPage/>} />
            <Route path="/product/create" element={<CreateProductPage/>} />
            <Route path="/product/edit/:id" element={<EditProductPage/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER", "CASHIER"]} />}>
            <Route path="/product/:id" element={<ProductPage/>} />
        </Route>
    </>
);

export { productRoutes }