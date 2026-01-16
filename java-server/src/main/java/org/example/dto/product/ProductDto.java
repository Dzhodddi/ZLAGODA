package org.example.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private int product_id;
    private String product_name;
    private String product_characteristics;
    private int category_number;
}
