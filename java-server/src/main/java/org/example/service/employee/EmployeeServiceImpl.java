package org.example.service.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.page.PageResponseDto;
import org.example.exception.custom_exception.DeletionException;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidRoleException;
import org.example.exception.custom_exception.RegistrationException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.model.employee.Role;
import org.example.model.employee.Employee;
import org.example.repository.employee.EmployeeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeResponseDto register(EmployeeRegistrationRequestDto request) throws RegistrationException {
        if (request.getId_employee() == null || request.getId_employee().isEmpty()) {
            throw new RegistrationException("Employee ID cannot be null or empty");
        }

        if (employeeRepository.existsByIdEmployee(request.getId_employee())) {
            throw new RegistrationException("Employee with such ID already exists: " + request.getId_employee());
        }

        LocalDate birthDate = request.getDate_of_birth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new RegistrationException("Employee must be at least 18 years old");
        }

        Employee employee = employeeMapper.toEmployeeEntity(request);
        employee.setId_employee(request.getId_employee());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        Role.RoleName roleEnum;
        try {
            roleEnum = Role.RoleName.valueOf(request.getRole().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role name: " + request.getRole());
        }

        if (employee.getSalary() == null) {
            employee.setSalary(BigDecimal.ZERO);
        }

        return employeeRepository.save(employee);
    }

    @Override
    public PageResponseDto<EmployeeResponseDto> getAll(Pageable pageable,
                                                       String lastSeenId) {
        return employeeRepository.findAll(pageable, lastSeenId);
    }


    @Override
    public EmployeeResponseDto updateEmployeeById(String id,
                                                  EmployeeUpdateRequestDto requestDto) {
        employeeRepository.findByIdEmployee(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update employee by id: " + id)
        );
        return employeeRepository.updateEmployeeById(id, requestDto);
    }

    @Override
    public void deleteEmployeeById(String id) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id.equals(currentUserId)) {
            throw new DeletionException("Cannot delete yourself by your own ID: " + id);
        }
        employeeRepository.findByIdEmployee(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot delete employee by id: " + id)
        );
        employeeRepository.deleteEmployeeById(id);
    }

    @Override
    public PageResponseDto<EmployeeResponseDto> getAllCashiers(Pageable pageable,
                                                               String lastSeenId) {
        return employeeRepository.findAllCashiers(pageable, lastSeenId);
    }

    @Override
    public EmployeeResponseDto getMe() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Employee> currentUser = employeeRepository.findByIdEmployee(currentUserId);
        return currentUser.map(employeeMapper::toEmployeeResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't find authorized cashier by id: " + currentUserId));
    }

    @Override
    public Optional<EmployeeContactDto> findPhoneAndAddressBySurname(String surname) {
        return Optional.ofNullable(employeeRepository.findPhoneAndAddressBySurname(surname)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find employee by surname: " + surname)));
    }

    @Override
    public List<EmployeeResponseDto> findAllNoPagination() {
        return employeeRepository.findAllNoPagination();
    }
}
