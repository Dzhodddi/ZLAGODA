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
WHERE category_number > $1
ORDER BY category_number, category_name
FETCH FIRST $2 ROWS ONLY;
