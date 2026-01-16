package org.example.dto.employee.registration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String shippingAddress;
}
