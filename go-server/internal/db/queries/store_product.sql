-- name: GetStoreProductIDList :many
SELECT UPC, selling_price, products_number, product_name
FROM store_product
JOIN product
ON store_product.id_product = product.id_product
WHERE products_number > 0;