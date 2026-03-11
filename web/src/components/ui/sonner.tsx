import { Toaster as Sonner, type ToasterProps } from "sonner"
import { CheckCircleIcon, InfoIcon, WarningIcon, XCircleIcon, SpinnerIcon } from "@phosphor-icons/react"

const Toaster = ({ ...props }: ToasterProps) => {

    return (
        <Sonner
            theme="light"
            position="top-right"
            className="toaster group"
            icons={{
                success: <CheckCircleIcon className="size-6 text-green-500" />,
                info: <InfoIcon className="size-6 text-blue-500" />,
                warning: <WarningIcon className="size-6 text-yellow-500" />,
                error: <XCircleIcon className="size-6 text-red-500" />,
                loading: <SpinnerIcon className="size-6 animate-spin text-zinc-500" />,
            }}
            style={
                {
                    "--normal-bg": "#ffffff",
                    "--normal-text": "#09090b",
                    "--normal-border": "#e4e4e7",
                    "--border-radius": "var(--radius)",
                    "--width": "600px",
                } as React.CSSProperties
            }
            toastOptions={{
                style: {
                    background: 'white',
                    opacity: 1,
                    boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
                    padding: '24px',
                    gap: '16px',
                },
                classNames: {
                    toast: "group toast group-[.toaster]:bg-white group-[.toaster]:text-zinc-950 group-[.toaster]:border-border group-[.toaster]:shadow-xl group-[.toaster]:opacity-100 text-base",
                    title: "text-lg font-semibold",
                    description: "group-[.toast]:text-zinc-500 text-sm",
                    actionButton: "group-[.toast]:bg-zinc-900 group-[.toast]:text-white px-4 py-2 text-sm",
                    cancelButton: "group-[.toast]:bg-zinc-100 group-[.toast]:text-zinc-500 px-4 py-2 text-sm",
                },
            }}
            {...props}
        />
    )
}

export { Toaster }