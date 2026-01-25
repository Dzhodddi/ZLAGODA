package org.example.dto.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeContactDto {
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
}
