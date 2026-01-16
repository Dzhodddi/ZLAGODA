-- name: CreateNewSale :one
INSERT INTO
    sale (product_number, upc, check_number, selling_price)
VALUES
    ($1, $2, $3, $4)
    RETURNING
	product_number, upc, check_number, selling_price;