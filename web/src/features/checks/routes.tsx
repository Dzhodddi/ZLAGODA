import { Route } from "react-router-dom";
import {UpsertCheckForm} from "@/features/checks/components/checkForm.tsx";



const checkRoutes = (
    <>
        <Route path="/check" element={<UpsertCheckForm/>} />
    </>
);

export { checkRoutes }