import { useEffect, useState } from "react";
import { useRole } from "@/hooks/useRole";
import { javaApiClient } from "@/lib/axios.ts";

interface StoreProduct {
    upc: string;
    upcProm: string | null;
    idProduct: number;
    sellingPrice: number;
    productsNumber: number;
    promotionalProduct: boolean;
    productName: string;
}

const MONO_LINK = "https://send.monobank.ua/jar/2JbpBYkhMv";

const cache = new Map<string, string>();

const useWikiImage = (name: string) => {
    const [url, setUrl] = useState("");

    useEffect(() => {
        if (!name) return;

        if (cache.has(name)) {
            setUrl(cache.get(name)!);
            return;
        }

        fetch(`https://uk.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(name)}&format=json&origin=*`)
            .then((r) => r.json())
            .then((data) => {
                const firstResult = data?.query?.search?.[0]?.title;
                if (!firstResult) return null;

                return fetch(
                    `https://uk.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(firstResult)}&prop=pageimages&format=json&pithumbsize=300&origin=*`
                );
            })
            .then((r) => r?.json())
            .then((data) => {
                if (!data) return;
                const page = Object.values(data?.query?.pages ?? {})[0] as any;
                const imageUrl = page?.thumbnail?.source;

                if (imageUrl) {
                    cache.set(name, imageUrl);
                    setUrl(imageUrl);
                }
            })
            .catch(() => {});
    }, [name]);

    return url;
};

const ProductCard = ({ product }: { product: StoreProduct }) => {
    const imageUrl = useWikiImage(product.productName);

    return (
        <a href={MONO_LINK} target="_blank" rel="noopener noreferrer"
           className="group flex flex-col rounded-xl border border-zinc-200 bg-white overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
        >
            <div className="relative w-full aspect-square bg-zinc-50 overflow-hidden">
                {!imageUrl && <div className="absolute inset-0 bg-zinc-100 animate-pulse" />}
                <img
                    src={imageUrl}
                    alt={product.productName}
                    className={`w-full h-full object-cover group-hover:scale-105 transition-transform duration-300 ${imageUrl ? "opacity-100" : "opacity-0"}`}
                    onError={(e) => {
                        (e.target as HTMLImageElement).src =
                            `https://placehold.co/300x300/f4f4f5/a1a1aa?text=${encodeURIComponent(product.productName)}`;
                    }}
                />
                {product.promotionalProduct && (
                    <span className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full z-10">
                        −20%
                    </span>
                )}
            </div>
            <div className="p-3 flex flex-col gap-1">
                <p className="text-sm font-medium text-zinc-800 line-clamp-2 leading-snug">
                    {product.productName}
                </p>
                <p className="text-base font-bold text-blue-700 mt-auto">
                    {Number(product.sellingPrice).toFixed(2)} ₴
                </p>
            </div>
        </a>
    );
};

export const Home = () => {
    const { isGuest } = useRole();
    const { isManager } = useRole();
    const { isCashier } = useRole();
    const [products, setProducts] = useState<StoreProduct[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isGuest) return;
        setLoading(true);
        javaApiClient.get("/store-products/all")
            .then((res) => setProducts(res.data?.content ?? res.data ?? []))
            .catch(() => setProducts([]))
            .finally(() => setLoading(false));
    }, [isGuest]);

    return (
        <div className="flex flex-col items-center justify-center">
            {(isManager || isCashier) && (
            <div className="flex flex-col md:flex-row items-center gap-12 max-w-5xl w-full pt-45 px-25">
                <div className="text-left">
                    <h1 className="text-4xl font-black text-zinc-900 leading-tight mb-4">
                        ZLAGODA <br />
                        <span className="text-blue-700">— МАГАЗИН ТВОГО ЖИТТЯ</span>
                    </h1>
                    <p className="text-zinc-600 text-lg leading-relaxed max-w-md">
                        Програмний застосунок для обробки даних, що використовуються
                        в процесі збуту товарів у продуктовому мінісупермаркеті
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
            )}

            {isGuest && (
                <div className="w-full max-w-7xl px-6">
                    {loading ? (
                        <div className="grid grid-cols-5 gap-4">
                            {Array.from({ length: 10 }).map((_, i) => (
                                <div
                                    key={i}
                                    className="rounded-xl bg-zinc-100 animate-pulse aspect-[3/4]"
                                />
                            ))}
                        </div>
                    ) : products.length === 0 ? (
                        <p className="text-zinc-400">Товарів не знайдено</p>
                    ) : (
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                            {products.slice(0, 20).map((p) => (
                                <ProductCard key={p.upc} product={p} />
                            ))}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};
