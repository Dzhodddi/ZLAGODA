import { useAuthStore } from "@/store/authStore";

export const useRole = () => {
    const role = useAuthStore((state) => state.role);
    return {
        isManager: role === "MANAGER",
        isCashier: role === "CASHIER",
        isGuest: role === null,
    };
};
