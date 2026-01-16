package org.example.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank
    private String productName;
    @NotBlank
    private String productCharacteristics;
    @NotNull
    private int categoryNumber;
}

