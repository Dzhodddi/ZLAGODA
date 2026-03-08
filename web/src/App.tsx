import "./index.css";

import {UpsertCategoryForm} from "@/features/category/components/categoryForm.tsx";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";
import {LoginForm} from "@/features/auth/components/loginForm.tsx";
import {PublicRoute} from "@/components/publicRoutes.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {UpsertEmployeeForm} from "@/features/employee/components/employeeForm.tsx";
import {UpsertProductForm} from "@/features/product/components/productForm.tsx";
import {UpsertStoreProductForm} from "@/features/store_product/components/storeProductForm.tsx";
import {RegistrationForm} from "@/features/auth/components/registrationForm.tsx";
import {StoreProductList} from "@/features/store_product/components/storeProductList";

export function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route element={<PublicRoute/>}>
                <Route path="/registration" element={<RegistrationForm />} />
                <Route path="/login" element={<LoginForm/>}/>
                <Route path="/store-product" element={<UpsertStoreProductForm/>}/>
            </Route>

            <Route element={<ProtectedRoute allowedRoles={["MANAGER", "CASHIER"]}/>}>
                <Route path="/store-product" element={<StoreProductList/>} />
                <Route path="/category" element={<UpsertCategoryForm/>}/>
                <Route path="/card" element={<UpsertCustomerCardForm/>}/>
                <Route path="/employee" element={<UpsertEmployeeForm/>}/>
                <Route path="/product" element={<UpsertProductForm/>}/>
            </Route>

            <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
                <Route path="/store-product/create" element={<UpsertStoreProductForm/>} />
                <Route path="/store-product/edit/:upc" element={<UpsertStoreProductForm/>} />
            </Route>

            <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
                <Route path="/employee/me" element={<UpsertStoreProductForm/>} />
            </Route>

            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/unauthorized" element={<h1>403 Forbidden</h1>} />
            <Route path="*" element={<h1>404 Page Not Found</h1>} />

            <Route path="*" element={<h1>Page Not Found</h1>} />
        </Routes>
    </BrowserRouter>
  );
}

export default App;
