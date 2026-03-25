import { Route } from "react-router-dom";
import {CategoryListPage} from "@/features/category/routes/CategoryListPage.tsx";
import {CreateCategoryPage} from "@/features/category/routes/CreateCategoryPage.tsx";
import {EditCategoryPage} from "@/features/category/routes/EditCategoryPage.tsx";
import {CategoryPage} from "@/features/category/routes/CategoryPage.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {TopCategoriesPage} from "@/features/category/routes/TopCategoriesPage.tsx";

const categoryRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/category" element={<CategoryListPage />} />
            <Route path="/category/create" element={<CreateCategoryPage />} />
            <Route path="/category/top" element={<TopCategoriesPage />} />
            <Route path="/category/edit/:id" element={<EditCategoryPage />} />
            <Route path="/category/:id" element={<CategoryPage />} />
        </Route>
    </>
);

export { categoryRoutes }