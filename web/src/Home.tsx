export const Home = () => {
    return (
        <div className="flex items-start justify-center min-h-screen pt-40">
            <div className="flex flex-col md:flex-row items-center gap-12 max-w-5xl">
                <div className="text-left">
                    <h1 className="text-4xl font-black text-zinc-900 leading-tight mb-4">
                        ZLAGODA <br />
                        <span className="text-blue-700">
                            — ВСЕ ДЛЯ ПІДТРИМКИ АРМІЇ
                        </span>
                    </h1>

                    <p className="text-zinc-600 text-lg leading-relaxed max-w-md">
                        Програмний застосунок для обробки даних, що використовуються
                        в процесі збуту товарів у продуктовому мінісупермаркеті
                    </p>

                    <p className="text-sm text-zinc-800 mt-6">
                        <a
                            href="https://smerch.com.ua/"
                            className="hover:underline"
                        >
                            Inspired by Сергій Стерненко
                        </a>
                    </p>
                </div>

                <div className="flex justify-center">
                    <img
                        src="/src/logos/icon-blue.png"
                        alt="Zlagoda logo"
                        className="w-65 h-65 object-contain"
                    />
                </div>

            </div>
        </div>
    );
};
