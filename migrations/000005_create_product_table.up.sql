CREATE TABLE IF NOT EXISTS product(
    id_product BIGSERIAL PRIMARY KEY,
    category_number INT NOT NULL,
    product_name VARCHAR(50) NOT NULL,
    product_characteristics VARCHAR(100) NOT NULL,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_number)
        REFERENCES category(category_number)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);