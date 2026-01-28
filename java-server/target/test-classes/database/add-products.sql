MERGE INTO category (category_number, category_name)
VALUES (1, 'Food'), (2, 'Hygiene');

MERGE INTO product (id_product, category_number, product_name, product_characteristics)
VALUES
    (1, 1, 'Milk', '1L, low-fat'),
    (2, 1, 'Bread', '500g, wheat'),
    (3, 2, 'Soap', '100g, moisturizing');