package org.example.service.employee;

import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;

public interface EmployeeService {
    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;
}
