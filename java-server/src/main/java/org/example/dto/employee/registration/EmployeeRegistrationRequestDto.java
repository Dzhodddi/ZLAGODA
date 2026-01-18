package org.example.dto.employee.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.employee.registration.annotation.FieldMatch;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "password",
        second = "repeatPassword",
        message = "Password and repeated password do not match")
public class EmployeeRegistrationRequestDto {
    @NotBlank
    @Length(min = 1, max = 10)
    private String idEmployee;
    @NotBlank
    @Length(min = 1, max = 50)
    private String emplSurname;
    @NotBlank
    @Length(min = 1, max = 50)
    private String emplName;
    @Length(min = 1, max = 50)
    private String emplPatronymic;
    @NotBlank
    private String role;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past
    private Date dateOfBirth;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfStart;
    @NotBlank
    @Length(min = 8, max = 13)
    private String phoneNumber;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;
    @NotBlank
    @Length(min = 1, max = 50)
    private String city;
    @NotBlank
    @Length(min = 1, max = 50)
    private String street;
    @NotBlank
    @Length(min = 3, max = 9)
    private String zipCode;
    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 100)
    private String password;
    @NotBlank(message = "Repeat password is required")
    @Length(min = 8, max = 100)
    private String repeatPassword;
}