import "./index.css";

import {UpsertCategoryForm} from "@/features/category/components/categoryForm.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {UpsertCustomerCardForm} from "@/features/customer-card/components/customerCardForm.tsx";
import {LoginForm} from "@/features/auth/components/loginForm.tsx";
import {PublicRoute} from "@/components/publicRoutes.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";

export function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route element={<PublicRoute/>}>
                <Route path="/login" element={<LoginForm/>}/>
            </Route>
            <Route element={<ProtectedRoute/>}>
                <Route path="/category" element={<UpsertCategoryForm/>}/>
                <Route path="/card" element={<UpsertCustomerCardForm/>}/>
            </Route>
            <Route path="*" element={<h1>Page Not Found</h1>} />
        </Routes>
    </BrowserRouter>
  );
}

export default App;
