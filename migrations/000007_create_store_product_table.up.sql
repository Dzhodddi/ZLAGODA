CREATE TABLE IF NOT EXISTS store_product (
    UPC VARCHAR(12) PRIMARY KEY,
    UPC_PROM VARCHAR(12) NULL,
    id_product INT NOT NULL,
    selling_price DECIMAL(13,4) NOT NULL,
    products_number INT NOT NULL,
    promotional_product BOOLEAN NOT NULL,
    FOREIGN KEY (UPC_prom)
    REFERENCES store_product(UPC)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
    FOREIGN KEY (id_product)
    REFERENCES product(id_product)
    ON UPDATE CASCADE
    ON DELETE NO ACTION
);