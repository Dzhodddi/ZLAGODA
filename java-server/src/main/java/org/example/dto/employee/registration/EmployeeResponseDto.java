package org.example.dto.employee.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class EmployeeResponseDto {
    private String id_employee;
    private String empl_surname;
    private String empl_name;
    private String empl_patronymic;
    private String role;
    private BigDecimal salary;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date_of_birth;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date_of_start;
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
}
