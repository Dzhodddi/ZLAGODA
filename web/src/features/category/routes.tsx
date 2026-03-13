import { Route } from "react-router-dom";
import {CategoryListPage} from "@/features/category/routes/CategoryListPage.tsx";
import {CreateCategoryPage} from "@/features/category/routes/CreateCategoryPage.tsx";
import {EditCategoryPage} from "@/features/category/routes/EditCategoryPage.tsx";
import {CategoryPage} from "@/features/category/routes/CategoryPage.tsx";


const categoryRoutes = (
    <>
        <Route path="/categories" element={<CategoryListPage />} />
        <Route path="/categories/create" element={<CreateCategoryPage />} />
        <Route path="/categories/edit/:id" element={<EditCategoryPage />} />
        <Route path="/categories/:id" element={<CategoryPage />} />
    </>
);

export { categoryRoutes }