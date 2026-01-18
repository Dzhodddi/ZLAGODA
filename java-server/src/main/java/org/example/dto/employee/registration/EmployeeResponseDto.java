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
    private String emplName;
    private String emplPatronymic;
    private String role;
    private BigDecimal salary;
    private Date dateOfBirth;
    private Date dateOfStart;
    private String phoneNumber;
    private String city;
    private String street;
    private String zipCode;
}
