package org.example.dto.store_product.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreProductRequestDto {
    @NotBlank
    private String UPC;
    private String UPC_prom;
    @NotNull
    private int id_product;
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal price;
    @Min(1)
    private int products_number;
    @NotNull
    private boolean promotional_product;
}
