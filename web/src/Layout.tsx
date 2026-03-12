import { Link, useNavigate, useLocation, Outlet } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";
import { useRole } from "@/hooks/useRole";

const navItems = [
    { path: "/store-product", label: "Товари в магазині" },
    { path: "/product", label: "Товари"},
    { path: "/categories", label: "Категорії" },
    { path: "/card", label: "Карти клієнтів" },
    { path: "/check", label: "Чеки" },
];

export const Layout = () => {
    const { clearTokens, role } = useAuthStore();
    const { isManager, isCashier } = useRole();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        clearTokens();
        navigate("/login");
    };

    const isActive = (path: string) =>
        location.pathname === path || location.pathname.startsWith(path + "/");

    return (
        <div className="flex flex-col h-screen overflow-hidden bg-zinc-100">
            <header className="bg-zinc-900 text-white px-6 py-3 flex justify-between items-center shadow-md z-20 shrink-0">
                <Link
                    to="/"
                    className="text-xl font-bold tracking-widest transition-colors"
                >
                    ZLAGODA
                </Link>
                <div className="flex flex-1 overflow-hidden">
                    <span className="text-xs px-2 py-2 rounded font-mono text-zinc-400">
                        {isManager ? "Менеджер" : "Касир"}
                    </span>
                    {isCashier && (
                        <Link
                            to="/employee/me"
                            className="text-sm hover:text-blue-300 transition-colors"
                        >
                            Мій профіль
                        </Link>
                    )}

                </div>
                <button
                    onClick={handleLogout}
                    className="px-3 py-1 rounded text-sm transition-colors inline-flex items-center gap-1"
                >
                    <img src="/src/logos/exit.png" alt="exit" className="w-4 h-4"/> Вийти
                </button>
            </header>

            <div className="flex flex-1 overflow-hidden">
                <aside className="w-52 bg-zinc-800 text-white flex flex-col py-6 px-3 gap-1 shrink-0 shadow-lg">
                    {navItems.map(({ path, label }) => (
                        <Link
                            key={path}
                            to={path}
                            className={`flex items-center gap-2 px-3 py-2 rounded text-sm transition-colors
                                ${isActive(path)
                                ? "bg-blue-600 text-white"
                                : "text-zinc-300 hover:bg-zinc-700 hover:text-white"
                            }`}
                        >
                            {label}
                        </Link>
                    ))}

                    {isManager && (
                        <Link
                            to="/employee"
                            className={`flex items-center gap-2 px-3 py-2 rounded text-sm transition-colors
                                ${isActive("/employee")
                                ? "bg-blue-600 text-white"
                                : "text-zinc-300 hover:bg-zinc-700 hover:text-white"
                            }`}
                        >
                            Працівники
                        </Link>
                    )}
                </aside>

                <main className="flex-1 overflow-y-auto p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};
