CREATE UNIQUE INDEX idx_one_promo_per_product
    ON Store_Product (id_product)
    WHERE promotional_product = true;

CREATE UNIQUE INDEX idx_one_regular_per_product
    ON Store_Product (id_product)
    WHERE promotional_product = false;

CREATE OR REPLACE FUNCTION check_store_product_limit()
    RETURNS TRIGGER AS $$
BEGIN
    IF (
           SELECT COUNT(*)
           FROM Store_Product
           WHERE id_product = NEW.id_product
       ) >= 2 THEN
        RAISE EXCEPTION
            'Товар % вже має 2 записи у Store_Product', NEW.id_product;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_store_product_limit
    BEFORE INSERT ON Store_Product
    FOR EACH ROW
EXECUTE FUNCTION check_store_product_limit();
