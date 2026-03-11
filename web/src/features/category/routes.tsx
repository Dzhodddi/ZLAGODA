import { Route } from "react-router-dom";
import {CategoryListPage} from "@/features/category/routes/CategoryListPage.tsx";
import {CreateCategoryPage} from "@/features/category/routes/CreateCategoryPage.tsx";
import {EditCategoryPage} from "@/features/category/routes/EditCategoryPage.tsx";


const categoryRoutes = (
    <>
        <Route path="/categories" element={<CategoryListPage />} />
        <Route path="/categories/new" element={<CreateCategoryPage />} />
        <Route path="/categories/edit/:id" element={<EditCategoryPage />} />
    </>
);

export { categoryRoutes }