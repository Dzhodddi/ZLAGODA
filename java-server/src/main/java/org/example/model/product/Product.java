package org.example.model.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Long idProduct;
    private String productName;
    private String productCharacteristics;
    private int categoryNumber;
}
