-- name: CreateNewCategory :one
INSERT INTO
    category (category_name)
VALUES
    ($1)
    RETURNING
	category_number,
	category_name;

-- name: UpdateCategory :one
UPDATE category
SET category_name = $2
WHERE category_number = $1
RETURNING category_number, category_name;

-- name: GetCategoryByID :one
SELECT category_number, category_name FROM category WHERE category_number = $1;

-- name: GetAllCategories :many
SELECT category_number, category_name
FROM category
WHERE category_number > $1
ORDER BY category_number
FETCH FIRST $2 ROWS ONLY;

-- name: DeleteCategoryByID :one
DELETE FROM category
WHERE category_number = $1
RETURNING category_number;

-- name: GetAllCategoriesSortedByName :many
SELECT category_number, category_name
FROM category
WHERE (category_name, category_number) > ($1, $2::bigint)
ORDER BY category_name ASC, category_number ASC
FETCH FIRST $3 ROWS ONLY;


-- name: GetCategoriesWithNoUnsoldProduct :many
SELECT
    category_number,
    category_name
FROM
    category с
WHERE
    NOT EXISTS (
        SELECT p.id_product
        FROM product p
        WHERE p.category_number = с.category_number
          AND NOT EXISTS (
            SELECT 1
            FROM sale s
                     JOIN store_product sp ON s.upc = sp.upc
            WHERE sp.id_product = p.id_product
        )
    );