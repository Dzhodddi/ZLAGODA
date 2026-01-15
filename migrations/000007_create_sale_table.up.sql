CREATE TABLE IF NOT EXISTS sale (
    product_number INT NOT NULL,
    UPC VARCHAR(12) NOT NULL,
    check_number VARCHAR(10) NOT NULL,
    selling_price DECIMAL(13,4) NOT NULL,
    PRIMARY KEY (UPC, check_number),
    FOREIGN KEY (UPC) REFERENCES store_product(UPC) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (check_number) REFERENCES checks(check_number) ON UPDATE CASCADE ON DELETE CASCADE
);
