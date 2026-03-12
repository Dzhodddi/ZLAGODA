import { Route } from "react-router-dom";
import {LoginForm} from "@/features/auth/components/loginForm.tsx";
import {RegistrationForm} from "@/features/auth/components/registrationForm.tsx";

const authRoutes = (
    <>
        <Route path="/login" element={<LoginForm />} />
        <Route path="/registration" element={<RegistrationForm />} />
    </>
);

export { authRoutes }