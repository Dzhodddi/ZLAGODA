CREATE TABLE IF NOT EXISTS check_store_product(
    check_number VARCHAR(10) NOT NULL,
    UPC VARCHAR(12) NOT NULL,
    selling_price DECIMAL(13,4) NOT NULL,
    quantity INT NOT NULL,
    check_date DATE NOT NULL,

    PRIMARY KEY (UPC, check_number, check_date),
    FOREIGN KEY (UPC) REFERENCES store_product(UPC) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (check_number) REFERENCES checks(check_number) ON UPDATE CASCADE ON DELETE NO ACTION
);