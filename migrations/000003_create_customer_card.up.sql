CREATE TABLE IF NOT EXISTS customer_card(
    card_number VARCHAR(13) PRIMARY KEY,
    customer_surname VARCHAR(50) NOT NULL,
    customer_name VARCHAR(50) NOT NULL,
    customer_patronymic VARCHAR(50),
    phone_number VARCHAR(13) NOT NULL,
    city VARCHAR(50),
    street VARCHAR(50),
    zip_code VARCHAR(9),
    customer_percent INT NOT NULL
);