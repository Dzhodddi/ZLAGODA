package org.example.dto.helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCardResponseDto {
    private String card_number;
    private String cust_surname;
    private String cust_name;
    private String cust_patronymic;
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
    private int percent;
}
