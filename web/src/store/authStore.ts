import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

type Role = "MANAGER" | "CASHIER" | null;

interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    role: Role;
    setTokens: (access: string, refresh: string, role: string) => void;
    clearTokens: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            accessToken: null,
            refreshToken: null,
            role: null,
            setTokens: (access, refresh, role) => set({ accessToken: access, refreshToken: refresh, role: role as Role }),
            clearTokens: () => set({ accessToken: null, refreshToken: null, role: null })
        }),
        {
            name: 'auth-storage',
            storage: createJSONStorage(() => sessionStorage)
        }
    )
);