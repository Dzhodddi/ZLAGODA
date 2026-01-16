package org.example.mapper.employee;

import org.example.config.MapperConfig;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface EmployeeMapper {
    EmployeeResponseDto toEmployeeResponseDto(Employee employee);

    Employee toEmployeeEntity(EmployeeRegistrationRequestDto requestDto);
}
