-- name: CreateNewCategory :one
INSERT INTO
    category (category_name)
VALUES
    ($1)
    RETURNING
	category_number,
	category_name;