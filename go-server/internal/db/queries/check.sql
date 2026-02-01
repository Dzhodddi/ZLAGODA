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
    s.selling_price,
    s.product_number
FROM
    checks c
        JOIN sale s ON c.check_number = s.check_number
        JOIN store_product sp ON s.upc = sp.upc
        JOIN product p ON p.id_product = sp.id_product
WHERE
    c.check_number = $1;

-- name: GetChecksWithProductsByCashierWithinDate :many
SELECT
    *
FROM
    check_list_view
WHERE
    id_employee = $1
    AND print_date BETWEEN $2 AND $3;

-- name: GetAllChecksWithProductsWithinDate :many
SELECT
    *
FROM
    check_list_view
WHERE
    print_date BETWEEN $1 AND $2;

-- name: GetTotalPriceByCashierWithinDate :one
SELECT
    sum(selling_price * product_number)::DOUBLE PRECISION as total_price
FROM
    check_list_view
WHERE
    id_employee = $1
    AND print_date BETWEEN $2 AND $3;

-- name: GetTotalPriceByAllCashiersWithinDate :one
SELECT
    sum(selling_price * product_number)::DOUBLE PRECISION as total_price
FROM
    check_list_view
WHERE
    print_date BETWEEN $1 AND $2;