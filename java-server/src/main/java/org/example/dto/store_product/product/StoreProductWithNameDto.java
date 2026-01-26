package org.example.dto.store_product.product;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreProductWithNameDto {
    private String UPC;
    private String UPC_prom;
    private int id_product;
    private BigDecimal selling_price;
    private int products_number;
    private boolean promotional_product;
    private String product_name;
}
