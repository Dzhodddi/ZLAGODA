package org.example.model.store_product;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Batch {
    private Long id;
    private String UPC;
    private Date delivery_date;
    private Date expiring_date;
    private int quantity;
    private BigDecimal selling_price;
}
