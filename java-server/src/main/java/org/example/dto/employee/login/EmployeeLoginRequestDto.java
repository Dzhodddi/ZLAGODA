package org.example.dto.employee.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeLoginRequestDto {
    @NotBlank
    @Size(min = 1, max = 10)
    private String idEmployee;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
