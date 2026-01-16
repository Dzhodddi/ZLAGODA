package org.example.dto.employee.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeLoginRequestDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String emplSurname;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
