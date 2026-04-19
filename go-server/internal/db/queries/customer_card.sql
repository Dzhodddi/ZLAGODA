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
FROM customer_card
WHERE card_number > $1
ORDER BY card_number
FETCH FIRST $2 ROWS ONLY;

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
WHERE (customer_surname, card_number) > (@CustomerSurname::varchar, @CardNumber::varchar)
ORDER BY customer_surname, card_number
FETCH FIRST $1 ROWS ONLY;

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
WHERE customer_percent = $1 and (customer_surname, card_number) > (@CustomerSurname::varchar, @CardNumber::varchar)
ORDER BY customer_surname, card_number
FETCH FIRST $2 ROWS ONLY;

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
WHERE card_number > $1 and customer_surname ILIKE $2
ORDER BY card_number
FETCH FIRST $3 ROWS ONLY;

-- name: GetCustomerCardIDList :many
SELECT card_number, CONCAT(customer_surname, ' ', customer_name)::VARCHAR as full_name
FROM customer_card;

-- name: GetCustomerFavoriteProducts :many
SELECT
    p.product_name,
    SUM(s.product_number) AS total_quantity_bought,
    SUM(s.product_number * s.selling_price)::DOUBLE PRECISION AS total_spent_on_product
FROM
    checks c
        JOIN sale s ON c.check_number = s.check_number
        JOIN store_product sp ON s.upc = sp.upc
        JOIN product p ON sp.id_product = p.id_product
WHERE
    c.card_number = $1
  AND c.print_date >= CURRENT_DATE - INTERVAL '3 years'
GROUP BY
    p.product_name
ORDER BY
    total_quantity_bought DESC, total_spent_on_product DESC;