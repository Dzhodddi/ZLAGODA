import { Route } from "react-router-dom";
import {SaleListPage} from "@/features/sales/routes/SaleListPage.tsx";


const saleRoutes = (
    <>
        <Route path="/sale" element={<SaleListPage />} />
    </>
);

export { saleRoutes }