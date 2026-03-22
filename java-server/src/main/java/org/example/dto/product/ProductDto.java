package org.example.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private int id_product;
    private String product_name;
    private String producer;
    private String product_characteristics;
    private int category_number;
    private String category_name;
    private int sold_number;
    private int total_sold;
}
