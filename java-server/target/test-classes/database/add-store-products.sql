INSERT INTO store_product (
    UPC, UPC_PROM, id_product, selling_price,
    products_number, promotional_product, is_deleted
) VALUES
      ('000000000001', NULL, 1, 25.00,
       50, false, false),

      ('000000000002', '000000000001', 2, 15.50,
       30, true, false),

      ('000000000003', NULL, 2, 20.00,
       20, false, false),

      ('000000000004', NULL, 3, 40.00,
       10, false, false);
