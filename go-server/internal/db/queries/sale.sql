-- name: GetSalesWithinDate :many
SELECT s.*
    FROM sale s
JOIN checks c
    ON c.check_number = s.check_number
WHERE
    c.print_date BETWEEN $1 AND $2
    AND (c.check_number, s.upc) > ($3, $4)
ORDER BY c.check_number
FETCH FIRST $5 ROWS ONLY;