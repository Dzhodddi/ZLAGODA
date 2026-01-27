package org.example.model.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private int id_product;
    private String product_name;
    private String product_characteristics;
    private int category_number;
}
