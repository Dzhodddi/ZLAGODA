package org.example.service.employee;

import java.util.List;

import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;

public interface EmployeeService {
    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;

    List<EmployeeResponseDto> getAll();

    EmployeeResponseDto updateEmployeeById(String id, EmployeeUpdateRequestDto requestDto);

    void deleteEmployeeById(String id);

    List<EmployeeResponseDto> getAllCashiers();

    EmployeeResponseDto getMe();

    EmployeeContactDto findPhoneAndAddressBySurname(String surname);
}
