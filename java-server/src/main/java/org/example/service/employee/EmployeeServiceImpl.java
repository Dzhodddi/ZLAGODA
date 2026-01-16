package org.example.service.employee;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.model.employee.Role;
import org.example.model.employee.Employee;
import org.example.repository.employee.RoleRepository;
import org.example.repository.employee.EmployeeRepository;
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
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public EmployeeResponseDto register(EmployeeRegistrationRequestDto request)
            throws RegistrationException {
        if (employeeRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RegistrationException(
                    "Employee with such email already exists: "
                            + request.getEmail());
        }
        Employee employee = employeeMapper.toEmployeeEntity(request);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        Role defaultRole = roleRepository.findRoleByName(Role.RoleName.CASHIER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role USER not found: " + Role.RoleName.CASHIER));
        employee.setRoles(Set.of(defaultRole));
        employeeRepository.save(employee);
        shoppingCartService.add(employee);
        return employeeMapper.toEmployeeResponseDto(employee);
    }
}
