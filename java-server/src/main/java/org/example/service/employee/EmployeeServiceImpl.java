package org.example.service.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.DeletionException;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.model.employee.Role;
import org.example.model.employee.Employee;
import org.example.repository.employee.RoleRepository;
import org.example.repository.employee.EmployeeRepository;
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
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public EmployeeResponseDto register(EmployeeRegistrationRequestDto request)
            throws RegistrationException {
        if (request.getIdEmployee() == null || request.getIdEmployee().isEmpty()) {
            throw new RegistrationException("Employee ID cannot be null or empty");
        }
        if (employeeRepository.existsByIdEmployee(request.getIdEmployee())) {
            throw new RegistrationException(
                    "Employee with such id already exists: "
                            + request.getIdEmployee());
        }
        LocalDate birthDate = request.getDate_of_birth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new RegistrationException("Employee must be at least 18 years old");
        }
        Employee employee = employeeMapper.toEmployeeEntity(request);
        if (employee.getIdEmployee() == null) {
            employee.setIdEmployee(request.getIdEmployee());
        }
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findById((long) request.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found with id: " + request.getRoleId()));
        employee.setRole(role);
        if (employee.getSalary() == null) {
            employee.setSalary(new BigDecimal("0.00"));
        }
        employeeRepository.save(employee);
        return employeeMapper.toEmployeeResponseDto(employee);
    }

    @Override
    public List<EmployeeResponseDto> getAll() {
        List<Employee> entities = employeeRepository.findAll();
        List<EmployeeResponseDto> res = new ArrayList<>();
        for (Employee entity : entities) {
            res.add(employeeMapper.toEmployeeResponseDto(entity));
        }
        return res;
    }

    @Override
    public EmployeeResponseDto updateEmployeeById(Long id, EmployeeRegistrationRequestDto requestDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update employee by id: " + id)
        );
        employeeMapper.updateEmployeeFromDto(requestDto, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toEmployeeResponseDto(updatedEmployee);
    }

    @Override
    public void deleteEmployeeById(String id) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (id.equals(currentUserId)) {
            throw new DeletionException("Cannot delete yourself by your own id: " + id);
        }
        Employee employee = employeeRepository.findByIdEmployee(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot delete employee by id: " + id)
        );
        employeeRepository.delete(employee);
    }
}
