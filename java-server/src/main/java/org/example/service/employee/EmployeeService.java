package org.example.service.employee;

import java.util.List;
import java.util.Optional;

import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;

    Page<EmployeeResponseDto> getAll(Pageable pageable);

    EmployeeResponseDto updateEmployeeById(String id, EmployeeUpdateRequestDto requestDto);

    void deleteEmployeeById(String id);

    Page<EmployeeResponseDto> getAllCashiers(Pageable pageable);

    EmployeeResponseDto getMe();

    Optional<EmployeeContactDto> findPhoneAndAddressBySurname(String surname);

    List<EmployeeResponseDto> findAllNoPagination();
}
