import "./index.css";

import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Suspense, lazy } from "react";
import { Layout } from "@/Layout";
import { Home } from "@/Home";
import { ProtectedRoute } from "@/components/protectedRoutes";
import { categoryRoutes } from "@/features/category/routes";
import {employeeRoutes} from "@/features/employee/routes.tsx";
import {storeProductRoutes} from "@/features/store_product/routes.tsx";
import {productRoutes} from "@/features/product/routes.tsx";
import {authRoutes} from "@/features/auth/routes.tsx";
import {checkRoutes} from "@/features/checks/routes.tsx";
import {customerCardRoutes} from "@/features/customer-card/routes.tsx";
import { useEffect } from "react";
import {useSilentRefresh} from "@/hooks/useSilentRefresh.ts";

const Toaster = lazy(() =>
    import("@/components/ui/sonner").then((module) => ({ default: module.Toaster }))
);

export function App() {
    const { isReady } = useSilentRefresh();

    useEffect(() => {
        document.title = "Zlagoda Shop";
    }, []);

    if (!isReady) {
        return null;
    }

    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    <Route path="/" element={<Home />} />
                    {authRoutes}
                </Route>

                <Route element={<ProtectedRoute />}>
                    <Route element={<Layout />}>
                        {categoryRoutes}
                        {employeeRoutes}
                        {storeProductRoutes}
                        {productRoutes}
                        {checkRoutes}
                        {customerCardRoutes}
                    </Route>
                </Route>
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