import "./index.css";

import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Suspense, lazy } from "react";
import { useAuthStore } from "@/store/authStore";
import { Layout } from "@/Layout";
import { Home } from "@/Home";
import { PublicRoute } from "@/components/publicRoutes";
import { ProtectedRoute } from "@/components/protectedRoutes";
import { LoginForm } from "@/features/auth/components/loginForm";
import { RegistrationForm } from "@/features/auth/components/registrationForm";
import { UpsertCategoryForm } from "@/features/category/components/categoryForm";
import { UpsertCustomerCardForm } from "@/features/customer-card/components/customerCardForm";
import { EmployeeList } from "@/features/employee/components/employeeList";
import { UpsertEmployeeForm } from "@/features/employee/components/employeeForm";
import { EmployeeMe } from "@/features/employee/components/employeeMe";
import { ProductList } from "@/features/product/components/productList";
import { UpsertProductForm } from "@/features/product/components/productForm";
import { StoreProductList } from "@/features/store_product/components/storeProductList";
import { UpsertStoreProductForm } from "@/features/store_product/components/storeProductForm";
import { UpsertCheckForm } from "@/features/checks/components/checkForm";
import { categoryRoutes } from "@/features/category/routes";

const Toaster = lazy(() =>
    import("@/components/ui/sonner").then((module) => ({ default: module.Toaster }))
);

export function App() {
    const { accessToken } = useAuthStore();

    return (
        <BrowserRouter>
            <Routes>
                <Route element={<PublicRoute />}>
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/registration" element={<RegistrationForm />} />
                </Route>

                <Route element={<ProtectedRoute />}>
                    <Route element={<Layout />}>
                        <Route path="/" element={<Home />} />
                        <Route path="/store-product" element={<StoreProductList/>} />
                        <Route path="/product" element={<ProductList/>} />
                        <Route path="/card" element={<UpsertCustomerCardForm/>} />
                        <Route path="/check" element={<UpsertCheckForm/>} />
                        <Route path="/category" element={<UpsertCategoryForm/>} />
                        {categoryRoutes}

                        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
                            <Route path="/store-product/create" element={<UpsertStoreProductForm/>} />
                            <Route path="/store-product/edit/:upc" element={<UpsertStoreProductForm/>} />
                            <Route path="/product/create" element={<UpsertProductForm/>} />
                            <Route path="/product/edit/:id" element={<UpsertProductForm/>} />
                            <Route path="/employee" element={<EmployeeList/>} />
                            <Route path="/employee/create" element={<UpsertEmployeeForm/>} />
                            <Route path="/employee/edit/:id" element={<UpsertEmployeeForm/>} />
                        </Route>

                        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
                            <Route path="/employee/me" element={<EmployeeMe />} />
                        </Route>
                    </Route>
                </Route>

                <Route
                    path="/login"
                    element={accessToken ? <Navigate to="/" replace /> : <LoginForm />}
                />
                <Route path="/unauthorized" element={<h1 className="p-8 text-2xl">403 Forbidden</h1>} />
                <Route path="*" element={<h1 className="p-8 text-2xl">404 Not Found</h1>} />
            </Routes>
            <Suspense fallback={null}>
                <Toaster />
            </Suspense>
        </BrowserRouter>
    );
}

export default App;