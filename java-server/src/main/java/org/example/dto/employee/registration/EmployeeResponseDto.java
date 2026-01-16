package org.example.dto.employee.registration;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class EmployeeResponseDto {
    private String emplSurname;
    private String empl_name;
    private Date date_of_birth;
    private Date date_of_start;
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
}
