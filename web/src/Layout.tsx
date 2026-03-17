import { Link, useNavigate, useLocation, Outlet } from "react-router-dom";
import { type Role, useAuthStore } from "@/store/authStore";
import { useRole } from "@/hooks/useRole";

const navItems = [
    { path: "/store-product", label: "Товари в магазині" },
    { path: "/product", label: "Товари" },
    { path: "/categories", label: "Категорії", isHidden: (role: Role) => role === "CASHIER" },
    { path: "/employee", label: "Працівники", isHidden: (role: Role) => role === "CASHIER" },
    { path: "/customer-card", label: "Картки клієнтів" },
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
                    <div className="flex items-center gap-2 group relative hover:text-zinc-300">
                        <div className="relative w-8 h-8">
                            <img
                                src="/src/logos/icon-red.png"
                                alt="Zlagoda logo"
                                className="w-8 h-8 group-hover:opacity-0"
                            />
                            <img
                                src="/src/logos/icon-red-hover.png"
                                alt="Zlagoda logo"
                                className="w-8 h-8 absolute inset-0 opacity-0 group-hover:opacity-100"
                            />
                        </div>
                        <span className="font-bold">ZLAGODA</span>
                    </div>
                </Link>
                <div className="flex flex-1 overflow-hidden">
                    <span className="text-xs px-2 py-2 rounded font-mono text-zinc-400">
                        {isManager ? "Менеджер" : "Касир"}
                    </span>
                    {isCashier && (
                        <Link
                            to="/employee/me"
                            className="group text-sm py-2 hover:text-blue-300 transition-colors"
                        >
                            <div className="hover:scale-110 transition-transform flex justify-center w-full">
                                <img
                                    src="/src/logos/profile.png"
                                    alt="my profile logo"
                                    className="w-6 h-6"
                                />
                            </div>
                        </Link>
                    )}
                </div>
                <button
                    onClick={handleLogout}
                    className="px-3 py-1 flex items-center gap-1 group relative hover:text-zinc-300"
                >
                    <div className="relative w-4 h-4 group">
                        <img src="/src/logos/exit.png" alt="exit" className="w-4 h-4 group-hover:opacity-0" />
                        <img src="/src/logos/exit-hover.png" alt="exit" className="w-4 h-4 absolute inset-0 opacity-0 group-hover:opacity-100" />
                    </div>
                    Вийти
                </button>
            </header>

            <div className="flex flex-1 overflow-hidden">
                <aside className="w-52 bg-zinc-800 text-white flex flex-col py-6 px-3 gap-1 shrink-0 shadow-lg">
                    {navItems
                        .filter(({ isHidden }) => {
                            return !isHidden || !isHidden(role);
                        })
                        .map(({ path, label }) => (
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
                </aside>

                <main className="flex-1 overflow-y-auto p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};