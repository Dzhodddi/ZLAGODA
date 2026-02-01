CREATE VIEW check_list_view AS
SELECT
    c.*,
    p.product_name,
    s.selling_price,
    s.product_number
FROM
    checks c
        JOIN sale s ON c.check_number = s.check_number
        JOIN store_product sp ON s.upc = sp.upc
        JOIN product p ON p.id_product = sp.id_product;