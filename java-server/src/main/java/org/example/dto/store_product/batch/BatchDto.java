package org.example.dto.store_product.batch;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class BatchDto {
    private Long id;
    private String UPC;
    private Date delivery_date;
    private Date expiring_date;
    private int quantity;
    private BigDecimal selling_price;
}
