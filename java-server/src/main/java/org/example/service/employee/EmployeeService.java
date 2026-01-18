package org.example.service.employee;

import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;

    List<EmployeeResponseDto> getAll();

    EmployeeResponseDto updateEmployeeById(String id, EmployeeRegistrationRequestDto requestDto);

    void deleteEmployeeById(String id);
}
