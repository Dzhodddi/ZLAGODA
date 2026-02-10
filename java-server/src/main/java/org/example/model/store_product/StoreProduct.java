package org.example.model.store_product;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreProduct {
    private String UPC;
    private String UPC_prom;
    private int id_product;
    private BigDecimal selling_price;
    private int products_number;
    private boolean promotional_product;
    private boolean is_deleted = Boolean.FALSE;

    public void setProducts_number(int products_number) {
        this.products_number = products_number;
        if (products_number == 0) {
            this.is_deleted = Boolean.TRUE;
        }
    }
}
