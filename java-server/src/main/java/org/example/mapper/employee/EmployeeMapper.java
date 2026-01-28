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

    default Role map(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return null;
        }
        Role role = new Role();
        role.setName(Role.RoleName.valueOf(roleName.toUpperCase().trim()));
        return role;
    }

    default String map(Role value) {
        if (value == null || value.getName() == null) {
            return null;
        }
        return value.getName().name();
    }
}