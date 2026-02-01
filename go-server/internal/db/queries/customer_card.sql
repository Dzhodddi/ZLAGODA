-- name: CreateNewCustomerCard :one
INSERT INTO
    customer_card (card_number, customer_surname, customer_name, customer_patronymic, phone_number, city, street, zip_code, customer_percent)
VALUES
    ($1, $2, $3, $4, $5, $6, $7, $8, $9)
    RETURNING
	card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent;

-- name: UpdateCustomerCard :one
UPDATE customer_card
SET
    customer_surname = $2,
    customer_name = $3,
    customer_patronymic = $4,
    phone_number = $5,
    city = $6,
    street = $7,
    zip_code = $8,
    customer_percent = $9
WHERE card_number = $1
RETURNING
	card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent;

-- name: GetCustomerCardByID :one
SELECT
    card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent
FROM customer_card
WHERE card_number = $1;

-- name: DeleteCustomerCardByID :one
DELETE FROM customer_card
WHERE card_number = $1
RETURNING card_number;

-- name: GetAllCustomerCards :many
SELECT
    card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent
FROM customer_card;

-- name: GetAllCustomerCardsSortedBySurname :many
SELECT
    card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent
FROM customer_card
ORDER BY customer_surname;

-- name: GetCustomerCardsByPercentSorted :many
SELECT
    card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent
FROM customer_card
WHERE customer_percent = $1
ORDER BY customer_surname;

-- name: SearchCustomerCardBySurname :many
SELECT
    card_number,
	customer_surname,
	customer_name,
	customer_patronymic,
	phone_number,
	city,
	street,
	zip_code,
	customer_percent
FROM customer_card
WHERE customer_surname ILIKE $1;