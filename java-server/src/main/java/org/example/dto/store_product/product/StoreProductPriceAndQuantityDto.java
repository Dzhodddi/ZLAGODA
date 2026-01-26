package org.example.dto.store_product.product;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreProductPriceAndQuantityDto {
    private BigDecimal selling_price;
    private int products_number;
}
