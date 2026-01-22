CREATE TABLE batch (
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
