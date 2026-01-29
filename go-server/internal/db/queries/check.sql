-- name: CreateNewCheck :one
INSERT INTO
    checks (check_number, id_employee, card_number, print_date, sum_total, vat)
VALUES
    ($1, $2, $3, $4, $5, $6)
RETURNING
	check_number,
	id_employee,
	card_number,
	print_date,
	sum_total,
	vat;

-- name: DeleteCheck :one
DELETE FROM checks WHERE check_number=$1
    RETURNING
	check_number,
	id_employee,
	card_number,
	print_date,
	sum_total,
	vat;

-- name: GetCheckByNumber :one
SELECT check_number, id_employee, card_number, print_date, sum_total, vat
FROM checks
WHERE check_number=$1;

-- name: GetCheckProductsByName :many
SELECT
    p.product_name,
    cp.selling_price,
    cp.quantity
FROM
    checks c
        JOIN check_store_product cp ON c.check_number = cp.check_number
        JOIN store_product sp ON cp.upc = sp.upc
        JOIN product p ON p.id_product = sp.id_product
WHERE
    c.check_number = $1;