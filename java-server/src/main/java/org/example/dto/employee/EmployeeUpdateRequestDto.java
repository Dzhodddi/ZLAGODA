package org.example.dto.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.employee.registration.annotation.date.CustomDateDeserializer;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequestDto {
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
    private String role;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date_of_birth;
    @NotNull
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date_of_start;
    @NotBlank
    @Length(min = 8, max = 13)
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
}
