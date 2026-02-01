-- name: GetSalesWithinDate :many
SELECT s.*
FROM sale s
JOIN checks c
ON c.check_number = s.check_number
WHERE c.print_date BETWEEN $1 AND $2;