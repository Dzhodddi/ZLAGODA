-- name: GetSalesWithinDate :many
SELECT s.*
FROM sale s
JOIN checks c
ON c.check_number = s.check_number
WHERE c.print_date BETWEEN $1 AND $2 AND c.check_number > $3
ORDER BY c.check_number
FETCH FIRST $4 ROWS ONLY;