package org.example.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank
    private String product_name;
    @NotBlank
    private String product_characteristics;
    @NotNull
    private int category_number;
}

