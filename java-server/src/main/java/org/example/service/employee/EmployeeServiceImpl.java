package org.example.service.employee;

import lombok.RequiredArgsConstructor;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.DeletionException;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.model.employee.Role;
import org.example.model.employee.Employee;
import org.example.repository.employee.EmployeeRepository;
import org.example.repository.employee.RoleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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

        try {
            Role.RoleName roleEnum = Role.RoleName.valueOf(request.getRole());
            Role role = new Role();
            role.setName(roleEnum);
            employee.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new RegistrationException("Invalid role name: " + request.getRole());
        }

        if (employee.getSalary() == null) {
            employee.setSalary(BigDecimal.ZERO);
        }

        return employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeResponseDto> getAll() {
        return employeeRepository.findAll();
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
}
