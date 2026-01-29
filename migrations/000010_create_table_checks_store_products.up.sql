CREATE TABLE IF NOT EXISTS check_store_product(
    id BIGSERIAL PRIMARY KEY,
    check_number VARCHAR(10),
    UPC VARCHAR(12),
    selling_price DECIMAL(13,4) NOT NULL,
    quantity INT NOT NULL,
    check_date DATE NOT NULL,

    FOREIGN KEY (UPC) REFERENCES store_product(UPC) ON UPDATE CASCADE ON DELETE SET NULL,
    FOREIGN KEY (check_number) REFERENCES checks(check_number) ON UPDATE CASCADE ON DELETE SET NULL
);