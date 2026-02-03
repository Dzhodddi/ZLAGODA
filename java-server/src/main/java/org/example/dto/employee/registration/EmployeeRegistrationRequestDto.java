package org.example.dto.employee.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.employee.registration.annotation.date.CustomDateDeserializer;
import org.example.dto.employee.registration.annotation.date.MinYear;
import org.example.dto.employee.registration.annotation.date.StartDateAfterBirth;
import org.example.dto.employee.registration.annotation.field_match.FieldMatch;
import org.example.dto.employee.registration.annotation.role.ValidRole;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "password",
        second = "repeat_password",
        message = "Password and repeated password do not match")
@StartDateAfterBirth(first = "date_of_birth",
        second = "date_of_start",
        message = "Date of start must be after date of birth")
public class EmployeeRegistrationRequestDto {
    @NotBlank
    @Length(min = 1, max = 10)
    private String id_employee;
    @NotBlank
    @Length(min = 1, max = 50)
    private String empl_surname;
    @NotBlank
    @Length(min = 1, max = 50)
    private String empl_name;
    @Length(min = 1, max = 50)
    private String empl_patronymic;
    @NotBlank
    @ValidRole
    private String role;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past
    @MinYear(1900)
    private Date date_of_birth;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date_of_start;
    @NotBlank
    @Length(min = 8, max = 13)
    @Pattern(
            regexp = "^(\\+380|0)\\d{9}$",
            message = "Phone number must be in format +380XXXXXXXXX or 0XXXXXXXXX"
    )
    private String phone_number;
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
    private String zip_code;
    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 100)
    private String password;
    @NotBlank(message = "Repeat password is required")
    @Length(min = 8, max = 100)
    private String repeat_password;
}
