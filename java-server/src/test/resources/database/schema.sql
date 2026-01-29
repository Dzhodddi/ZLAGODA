CREATE TABLE IF NOT EXISTS roles (
                                     name VARCHAR(10) PRIMARY KEY
    );

INSERT INTO roles (name) VALUES ('MANAGER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('CASHIER') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS employee(
    id_employee VARCHAR(10) PRIMARY KEY,
    empl_surname VARCHAR(50) NOT NULL,
    empl_name VARCHAR(50) NOT NULL,
    empl_patronymic VARCHAR(50),
    empl_role VARCHAR(10) NOT NULL REFERENCES roles(name),
    empl_salary DECIMAL(13,4) NOT NULL,
    date_of_birth DATE NOT NULL,
    date_of_start DATE NOT NULL,
    phone_number VARCHAR(13) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    zip_code VARCHAR(9) NOT NULL,
    password VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS category (
     category_number BIGSERIAL PRIMARY KEY,
     category_name VARCHAR(50) NOT NULL
    );

INSERT INTO category (category_number, category_name)
VALUES
    (1, 'Dairy'),
    (2, 'Beverages'),
    (3, 'Snacks');

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

CREATE TABLE IF NOT EXISTS store_product (
    UPC VARCHAR(12) PRIMARY KEY,
    UPC_PROM VARCHAR(12) NULL,
    id_product INT NOT NULL,
    selling_price DECIMAL(13,4) NOT NULL,
    products_number INT NOT NULL,
    promotional_product BOOLEAN NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    FOREIGN KEY (UPC_prom)
    REFERENCES store_product(UPC)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
    FOREIGN KEY (id_product)
    REFERENCES product(id_product)
    ON UPDATE CASCADE
    ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS batch (
    id SERIAL PRIMARY KEY,
    UPC VARCHAR(12) NOT NULL,
    delivery_date DATE NOT NULL,
    expiring_date DATE NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    selling_price DECIMAL(13,4) NOT NULL,
    FOREIGN KEY (UPC)
    REFERENCES store_product(UPC)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
