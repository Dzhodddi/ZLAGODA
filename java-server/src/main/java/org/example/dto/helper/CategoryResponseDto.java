package org.example.dto.helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {
    private int category_number;
    private String category_name;
    private Integer total_sold;
}
