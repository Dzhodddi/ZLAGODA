package org.example.mapper.employee;

import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEmployeeEntity(EmployeeRegistrationRequestDto dto);

    EmployeeResponseDto toEmployeeResponseDto(Employee employee);

    default Role map(String value) {
        if (value == null) {
            return null;
        }
        Role role = new Role();
        try {
            role.setName(Role.RoleName.valueOf(value));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name: " + value);
        }
        return role;
    }

    default String map(Role value) {
        if (value == null || value.getName() == null) {
            return null;
        }
        return value.getName().name();
    }
}