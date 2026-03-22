import { Route } from "react-router-dom";
import { ProtectedRoute } from "@/components/protectedRoutes.tsx";
import { StoreProductList } from "@/features/store_product/routes/StoreProductList.tsx";
import { CreateStoreProductPage } from "@/features/store_product/routes/CreateStoreProductPage.tsx";
import { EditStoreProductPage } from "@/features/store_product/routes/EditStoreProductPage.tsx";
import { StoreProductPage } from "@/features/store_product/routes/StoreProductPage.tsx";
import { ReceiveBatchPage } from "@/features/store_product/routes/ReceiveBatchPage.tsx";

const storeProductRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/store-product/create" element={<CreateStoreProductPage />} />
            <Route path="/store-product/edit/:upc" element={<EditStoreProductPage />} />
            <Route path="/store-product/receive/:upc" element={<ReceiveBatchPage />} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER", "CASHIER"]} />}>
            <Route path="/store-product" element={<StoreProductList />} />
            <Route path="/store-product/:upc" element={<StoreProductPage />} />
        </Route>
    </>
);

export { storeProductRoutes };
