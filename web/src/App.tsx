import "./index.css";

import {BrowserRouter, Route, Routes} from "react-router-dom";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";
import {LoginForm} from "@/features/auth/components/loginForm.tsx";
import {PublicRoute} from "@/components/publicRoutes.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {UpsertCheckForm} from "@/features/checks/components/checkForm.tsx";
import {categoryRoutes} from "@/features/category/routes.tsx";
import {Suspense, lazy} from "react";
const Toaster = lazy(() =>
    import("@/components/ui/sonner").then((module) => ({ default: module.Toaster }))
);

export function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route element={<PublicRoute/>}>
                <Route path="/login" element={<LoginForm/>}/>
            </Route>
            <Route element={<ProtectedRoute/>}>
                {categoryRoutes}
                <Route path="/card" element={<UpsertCustomerCardForm/>}/>
                <Route path="/check" element={<UpsertCheckForm/>}/>
            </Route>
            <Route path="*" element={<h1>Page Not Found</h1>} />
        </Routes>
        <Suspense fallback={null}>
            <Toaster/>
        </Suspense>
    </BrowserRouter>
  );
}

export default App;
