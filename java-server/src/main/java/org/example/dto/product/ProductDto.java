package org.example.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private int idProduct;
    private String productName;
    private String productCharacteristics;
    private int categoryNumber;
}
