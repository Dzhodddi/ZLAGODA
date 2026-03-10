import { createContext, useContext, useState } from "react";

type Role = "MANAGER" | "CASHIER" | null;

interface AuthContextType {
    token: string | null;
    role: Role;
    login: (token: string, role: Role) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType>(null!);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [token, setToken] = useState<string | null>(
        localStorage.getItem("token")
    );
    const [role, setRole] = useState<Role>(
        localStorage.getItem("role") as Role
    );

    const login = (token: string, role: Role) => {
        localStorage.setItem("token", token);
        localStorage.setItem("role", role!);
        setToken(token);
        setRole(role);
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        setToken(null);
        setRole(null);
    };

    return (
        <AuthContext.Provider value={{ token, role, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
