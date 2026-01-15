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