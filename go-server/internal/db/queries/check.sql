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
    product_name,
    selling_price,
    product_number
FROM
    check_list_view
WHERE
    check_number = $1;


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

-- name: GetChecksByCashierWithinDate :many
SELECT
    check_number,
    id_employee,
    card_number,
    print_date,
    sum_total,
    vat
FROM
    checks
WHERE
    id_employee = $1
  AND print_date BETWEEN $2 AND $3
  AND check_number > $4
ORDER BY
    check_number
FETCH FIRST $5 ROWS ONLY;

-- name: GetAllChecksWithinDate :many
SELECT
    check_number,
    id_employee,
    card_number,
    print_date,
    sum_total,
    vat
FROM
    checks
WHERE
    print_date BETWEEN $1 AND $2
  AND check_number > $3
ORDER BY
    check_number
FETCH FIRST $4 ROWS ONLY;

-- name: GetTotalPriceByCashierWithinDate :one
SELECT sum(total_price)::DOUBLE PRECISION as total_price from (
    SELECT
        (sum(c.sum_total) / count(c.check_number))::DOUBLE PRECISION as total_price
    FROM
        checks c
            JOIN sale s ON c.check_number = s.check_number
    WHERE
        id_employee = $1 AND
        print_date BETWEEN $2 AND $3
    GROUP BY c.check_number
);

-- name: GetTotalPriceByAllCashiersWithinDate :one
SELECT sum(total_price)::DOUBLE PRECISION as total_price from (
    SELECT
       (sum(c.sum_total) / count(c.check_number))::DOUBLE PRECISION as total_price
    FROM
        checks c
            JOIN sale s ON c.check_number = s.check_number
    WHERE
        print_date BETWEEN $1 AND $2
    GROUP BY c.check_number
);