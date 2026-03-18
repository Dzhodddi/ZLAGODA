package org.example.dto.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
}
