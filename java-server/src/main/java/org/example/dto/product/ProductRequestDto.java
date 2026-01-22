package org.example.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String product_name;
    @NotBlank
    @Length(min = 1, max = 100)
    private String product_characteristics;
    @NotNull
    private int category_number;
}

