import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";
import { useRole } from "@/hooks/useRole";

export const Navbar = () => {
    const { clearTokens } = useAuthStore();
    const { isManager, isCashier } = useRole();
    const navigate = useNavigate();

    const handleLogout = () => {
        clearTokens();
        navigate("/login");
    };

    return (
        <nav className="bg-zinc-800 text-white px-6 py-3 flex gap-4 items-center">
            <Link to="/store-product" className="hover:text-blue-300 text-sm">Продукти в магазині</Link>
            <Link to="/card" className="hover:text-blue-300 text-sm">Карти клієнтів</Link>
            <Link to="/product" className="hover:text-blue-300 text-sm">Продукти</Link>
            <Link to="/category" className="hover:text-blue-300 text-sm">Категорії</Link>

            {isManager && (
                <Link to="/employee" className="hover:text-blue-300 text-sm">Працівники</Link>
            )}

            {isCashier && (
                <Link to="/employee/me" className="hover:text-blue-300 text-sm">Мій профіль</Link>
            )}

            <div className="ml-auto">
                <button
                    onClick={handleLogout}
                    className="bg-red-600 px-3 py-1 rounded hover:bg-red-700 text-sm"
                >
                    Вийти
                </button>
            </div>
        </nav>
    );
};
