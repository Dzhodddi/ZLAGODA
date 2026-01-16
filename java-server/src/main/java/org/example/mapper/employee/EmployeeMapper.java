package org.example.mapper.employee;

import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "idEmployee", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(source = "empl_name", target = "emplName")
    @Mapping(source = "empl_patronymic", target = "emplPatronymic")
    @Mapping(source = "date_of_birth", target = "dateOfBirth")
    @Mapping(source = "date_of_start", target = "dateOfStart")
    @Mapping(source = "phone_number", target = "phoneNumber")
    @Mapping(source = "zip_code", target = "zipCode")
    @Mapping(source = "password", target = "password")
    Employee toEmployeeEntity(EmployeeRegistrationRequestDto dto);

    @Mapping(source = "emplName", target = "empl_name")
    @Mapping(source = "dateOfBirth", target = "date_of_birth")
    @Mapping(source = "dateOfStart", target = "date_of_start")
    @Mapping(source = "phoneNumber", target = "phone_number")
    @Mapping(source = "zipCode", target = "zip_code")
    EmployeeResponseDto toEmployeeResponseDto(Employee employee);

    @Mapping(target = "idEmployee", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "empl_name", target = "emplName")
    @Mapping(source = "empl_patronymic", target = "emplPatronymic")
    @Mapping(source = "date_of_birth", target = "dateOfBirth")
    @Mapping(source = "date_of_start", target = "dateOfStart")
    @Mapping(source = "phone_number", target = "phoneNumber")
    @Mapping(source = "zip_code", target = "zipCode")
    void updateEmployeeFromDto(EmployeeRegistrationRequestDto requestDto,
                              @MappingTarget Employee employee);
}