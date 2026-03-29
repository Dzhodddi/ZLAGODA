package org.example.dto.helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckResponseDto {
    private String check_number;
    private String id_employee;
    private String card_number;
    private LocalDateTime print_date;
    private BigDecimal sum_total;
    private BigDecimal vat;
}
