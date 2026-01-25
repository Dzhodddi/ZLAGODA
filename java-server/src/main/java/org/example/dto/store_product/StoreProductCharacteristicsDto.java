package org.example.dto.store_product;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class StoreProductCharacteristicsDto {
    private BigDecimal selling_price;
    private int products_number;
    private String product_name;
    private String product_characteristics;
}
