--name: CreateNewSale :one
INSERT INTO sale (product_number, UPC, check_number, selling_price)
VALUES ($1, $2, $3, $4)
RETURNING UPC, check_number, product_number, selling_price;
