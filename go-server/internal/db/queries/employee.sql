-- name: EmployeeExistsByID :one
SELECT
    EXISTS (
        SELECT
            1
        FROM
            employee
        WHERE
            id_employee = $1
          AND empl_role = 'CASHIER'
    );

-- name: GetEmployeeByID :one
SELECT id_employee, empl_name, empl_role
FROM employee
WHERE id_employee = $1;