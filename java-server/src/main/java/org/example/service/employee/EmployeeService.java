package org.example.service.employee;

import java.util.List;
import java.util.Optional;

import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.page.PageResponseDto;
import org.example.exception.custom_exception.RegistrationException;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Optional<EmployeeResponseDto> getEmployee(String id);

    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;

    PageResponseDto<EmployeeResponseDto> getAll(Pageable pageable);

    EmployeeResponseDto updateEmployeeById(String id, EmployeeUpdateRequestDto requestDto);

    void deleteEmployeeById(String id);

    PageResponseDto<EmployeeResponseDto> getAllCashiers(Pageable pageable);

    EmployeeResponseDto getMe();

    PageResponseDto<EmployeeContactDto> findPhoneAndAddressBySurname(String surname,
                                                                     Pageable pageable);

    List<EmployeeResponseDto> findAllNoPagination();
}
