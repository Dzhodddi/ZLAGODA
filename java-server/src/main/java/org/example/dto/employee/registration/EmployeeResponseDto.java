package org.example.dto.employee.registration;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class EmployeeResponseDto {
    private String idEmployee;
    private String emplSurname;
    private String empl_name;
    private BigDecimal salary;
    private Date date_of_birth;
    private Date date_of_start;
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
}
