package org.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.example.repository.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Custom User Details Service Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    private Employee employee;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(Role.RoleName.MANAGER);

        employee = new Employee();
        employee.setId_employee("EMP001");
        employee.setEmpl_surname("Іваненко");
        employee.setEmpl_name("Іван");
        employee.setEmpl_patronymic("Іванович");
        employee.setRole(role);
        employee.setSalary(new BigDecimal("15000.00"));
        employee.setDate_of_birth(Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        employee.setDate_of_start(Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        employee.setPhone_number("+380501234567");
        employee.setCity("Київ");
        employee.setStreet("Хрещатик 1");
        employee.setZip_code("01001");
        employee.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when employee exists")
    void loadUserByUsername_existingEmployee_shouldReturnUserDetails() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP001");

        assertNotNull(result);
        assertEquals("EMP001", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        verify(employeeRepository, times(1)).findByIdEmployee("EMP001");
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when employee not found")
    void loadUserByUsername_nonExistingEmployee_shouldThrowException() {
        when(employeeRepository.findByIdEmployee("EMP999")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("EMP999"));

        assertTrue(exception.getMessage().contains("EMP999"));
        assertTrue(exception.getMessage().contains("not found"));
        verify(employeeRepository, times(1)).findByIdEmployee("EMP999");
    }

    @Test
    @DisplayName("loadUserByUsername should return employee with correct authorities")
    void loadUserByUsername_shouldReturnCorrectAuthorities() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP001");

        assertNotNull(result);
        assertNotNull(result.getAuthorities());
        assertFalse(result.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("loadUserByUsername should work with CASHIER role")
    void loadUserByUsername_cashierRole_shouldReturnUserDetails() {
        Role cashierRole = new Role();
        cashierRole.setName(Role.RoleName.CASHIER);
        employee.setRole(cashierRole);
        employee.setId_employee("EMP002");

        when(employeeRepository.findByIdEmployee("EMP002")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP002");

        assertNotNull(result);
        assertEquals("EMP002", result.getUsername());
        verify(employeeRepository, times(1)).findByIdEmployee("EMP002");
    }

    @Test
    @DisplayName("loadUserByUsername should return enabled user")
    void loadUserByUsername_shouldReturnEnabledUser() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP001");

        assertTrue(result.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername should return non-expired user")
    void loadUserByUsername_shouldReturnNonExpiredUser() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP001");

        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("loadUserByUsername should use employee ID as username")
    void loadUserByUsername_shouldUseIdAsUsername() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        UserDetails result = service.loadUserByUsername("EMP001");

        assertEquals(employee.getId_employee(), result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername should call repository with correct ID")
    void loadUserByUsername_shouldCallRepositoryWithCorrectId() {
        when(employeeRepository.findByIdEmployee("CUSTOM_ID")).thenReturn(Optional.of(employee));

        service.loadUserByUsername("CUSTOM_ID");

        verify(employeeRepository, times(1)).findByIdEmployee("CUSTOM_ID");
    }
}
