package org.example.service.employee;

import java.util.List;

import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.page.PageResponseDto;
import org.example.exception.custom_exception.RegistrationException;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException;

    PageResponseDto<EmployeeResponseDto> getAll(Pageable pageable,
                                                String lastSeenId);

    EmployeeResponseDto updateEmployeeById(String id, EmployeeUpdateRequestDto requestDto);

    void deleteEmployeeById(String id);

    PageResponseDto<EmployeeResponseDto> getAllCashiers(Pageable pageable,
                                             String lastSeenId);

    EmployeeResponseDto getMe();

    PageResponseDto<EmployeeContactDto> findPhoneAndAddressBySurname(String surname,
                                                                     Pageable pageable,
                                                                     String lastSeenId);

    List<EmployeeResponseDto> findAllNoPagination();
}
