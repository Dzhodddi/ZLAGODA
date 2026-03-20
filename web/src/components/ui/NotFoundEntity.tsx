import {Link} from "react-router-dom";


interface Props {
    redirectTiList: string
    title: string
    message: string
}

export const NotFoundEntity = ({redirectTiList, title, message}: Props) => {
    return (
        <div className="p-8 flex flex-col items-center justify-center space-y-4">
            <h2 className="text-2xl font-bold text-red-500">{title}</h2>
            <p className="text-zinc-600">{message}</p>
            <Link
                to={redirectTiList}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
            >
                Повернутися до списку
            </Link>
        </div>
    )
}