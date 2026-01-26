package org.example.dto.store_product.batch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.employee.registration.annotation.date.CustomDateDeserializer;

@Getter
@Setter
public class BatchRequestDto {
    @NotBlank
    private String UPC;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date delivery_date;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date expiring_date;
    @Min(1)
    private int quantity;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}
