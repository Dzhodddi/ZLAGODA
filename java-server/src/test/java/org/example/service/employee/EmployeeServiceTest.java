package org.example.service.employee;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Service Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeServiceImpl service;

    private Employee employee;
    private EmployeeResponseDto employeeResponseDto;
    private EmployeeRegistrationRequestDto registrationRequestDto;
    private EmployeeUpdateRequestDto updateRequestDto;
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

        employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setId_employee("EMP001");
        employeeResponseDto.setEmpl_surname("Іваненко");
        employeeResponseDto.setEmpl_name("Іван");
        employeeResponseDto.setRole("MANAGER");

        registrationRequestDto = new EmployeeRegistrationRequestDto();
        registrationRequestDto.setId_employee("EMP001");
        registrationRequestDto.setEmpl_surname("Іваненко");
        registrationRequestDto.setEmpl_name("Іван");
        registrationRequestDto.setEmpl_patronymic("Іванович");
        registrationRequestDto.setRole("MANAGER");
        registrationRequestDto.setSalary(new BigDecimal("15000.00"));
        registrationRequestDto.setDate_of_birth(Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        registrationRequestDto.setDate_of_start(Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        registrationRequestDto.setPhone_number("+380501234567");
        registrationRequestDto.setCity("Київ");
        registrationRequestDto.setStreet("Хрещатик 1");
        registrationRequestDto.setZip_code("01001");
        registrationRequestDto.setPassword("password123");

        updateRequestDto = new EmployeeUpdateRequestDto();
        updateRequestDto.setEmpl_surname("Петренко");
        updateRequestDto.setEmpl_name("Петро");
        updateRequestDto.setRole("MANAGER");
    }

    @Test
    @DisplayName("register should successfully register new employee")
    void register_validEmployee_shouldReturnResponseDto() throws RegistrationException {
        when(employeeRepository.existsByIdEmployee("EMP001")).thenReturn(false);
        when(employeeMapper.toEmployeeEntity(registrationRequestDto)).thenReturn(employee);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = service.register(registrationRequestDto);

        assertNotNull(result);
        assertEquals("EMP001", result.getId_employee());
        verify(employeeRepository, times(1)).existsByIdEmployee("EMP001");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("register should throw RegistrationException when ID is null")
    void register_nullId_shouldThrowException() {
        registrationRequestDto.setId_employee(null);

        assertThrows(RegistrationException.class, () -> service.register(registrationRequestDto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw RegistrationException when ID is empty")
    void register_emptyId_shouldThrowException() {
        registrationRequestDto.setId_employee("");

        assertThrows(RegistrationException.class, () -> service.register(registrationRequestDto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw RegistrationException when employee already exists")
    void register_existingEmployee_shouldThrowException() {
        when(employeeRepository.existsByIdEmployee("EMP001")).thenReturn(true);

        assertThrows(RegistrationException.class, () -> service.register(registrationRequestDto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw RegistrationException when employee is under 18")
    void register_underAge_shouldThrowException() {
        registrationRequestDto.setDate_of_birth(Date.from(LocalDate.now().minusYears(17)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(employeeRepository.existsByIdEmployee("EMP001")).thenReturn(false);

        assertThrows(RegistrationException.class, () -> service.register(registrationRequestDto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw InvalidRoleException for invalid role")
    void register_invalidRole_shouldThrowException() {
        registrationRequestDto.setRole("INVALID_ROLE");
        when(employeeRepository.existsByIdEmployee("EMP001")).thenReturn(false);
        when(employeeMapper.toEmployeeEntity(registrationRequestDto)).thenReturn(employee);

        assertThrows(InvalidRoleException.class, () -> service.register(registrationRequestDto));
    }

    @Test
    @DisplayName("register should set salary to zero if null")
    void register_nullSalary_shouldSetToZero() throws RegistrationException {
        registrationRequestDto.setSalary(null);
        employee.setSalary(null);

        when(employeeRepository.existsByIdEmployee("EMP001")).thenReturn(false);
        when(employeeMapper.toEmployeeEntity(registrationRequestDto)).thenReturn(employee);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeResponseDto);

        service.register(registrationRequestDto);

        verify(employeeRepository, times(1)).save(argThat(emp ->
                emp.getSalary() != null && emp.getSalary().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    @DisplayName("getAll should return list of employees")
    void getAll_shouldReturnList() {
        PageResponseDto<EmployeeResponseDto> page = PageResponseDto.of(
                List.of(employeeResponseDto),
                0,
                10,
                false
        );
        Pageable pageable = Pageable.ofSize(10);
        when(employeeRepository.findAll(pageable, null))
                .thenReturn(page);
        PageResponseDto<EmployeeResponseDto> result = service
                .getAll(pageable, null);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("EMP001", result.getContent().get(0).getId_employee());
        verify(employeeRepository, times(1))
                .findAll(pageable, null);
    }

    @Test
    @DisplayName("updateEmployeeById should update and return employee")
    void updateEmployeeById_existingEmployee_shouldReturnUpdated() {
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));
        when(employeeRepository.updateEmployeeById("EMP001", updateRequestDto))
                .thenReturn(employeeResponseDto);

        EmployeeResponseDto result = service.updateEmployeeById("EMP001", updateRequestDto);

        assertNotNull(result);
        assertEquals("EMP001", result.getId_employee());
        verify(employeeRepository, times(1))
                .updateEmployeeById("EMP001", updateRequestDto);
    }

    @Test
    @DisplayName("updateEmployeeById should throw EntityNotFoundException when employee not found")
    void updateEmployeeById_nonExistingEmployee_shouldThrowException() {
        when(employeeRepository.findByIdEmployee("EMP999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updateEmployeeById("EMP999", updateRequestDto));
        verify(employeeRepository, never()).updateEmployeeById(anyString(), any());
    }

    @Test
    @DisplayName("deleteEmployeeById should delete employee")
    void deleteEmployeeById_existingEmployee_shouldDelete() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP002");
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));

        service.deleteEmployeeById("EMP001");

        verify(employeeRepository, times(1)).deleteEmployeeById("EMP001");
    }

    @Test
    @DisplayName("deleteEmployeeById should throw DeletionException when deleting self")
    void deleteEmployeeById_deletingSelf_shouldThrowException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP001");

        assertThrows(DeletionException.class, () -> service.deleteEmployeeById("EMP001"));
        verify(employeeRepository, never()).deleteEmployeeById(anyString());
    }

    @Test
    @DisplayName("deleteEmployeeById should throw EntityNotFoundException when employee not found")
    void deleteEmployeeById_nonExistingEmployee_shouldThrowException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP002");
        when(employeeRepository.findByIdEmployee("EMP999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteEmployeeById("EMP999"));
        verify(employeeRepository, never()).deleteEmployeeById(anyString());
    }

    @Test
    @DisplayName("getAllCashiers should return list of cashiers")
    void getAllCashiers_shouldReturnCashiers() {
        PageResponseDto<EmployeeResponseDto> page = PageResponseDto.of(
                List.of(employeeResponseDto),
                0,
                10,
                false
        );
        Pageable pageable = Pageable.ofSize(10);
        when(employeeRepository.findAllCashiers(pageable, null))
                .thenReturn(page);

        PageResponseDto<EmployeeResponseDto> result = service
                .getAllCashiers(pageable, null);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(employeeRepository, times(1))
                .findAllCashiers(pageable, null);
    }

    @Test
    @DisplayName("getMe should return current employee")
    void getMe_shouldReturnCurrentEmployee() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP001");
        when(employeeRepository.findByIdEmployee("EMP001")).thenReturn(Optional.of(employee));
        when(employeeMapper.toEmployeeResponseDto(employee)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = service.getMe();

        assertNotNull(result);
        assertEquals("EMP001", result.getId_employee());
        verify(employeeRepository, times(1)).findByIdEmployee("EMP001");
    }

    @Test
    @DisplayName("getMe should throw EntityNotFoundException when employee not found")
    void getMe_notFound_shouldThrowException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP999");
        when(employeeRepository.findByIdEmployee("EMP999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getMe());
    }

    @Test
    @DisplayName("findPhoneAndAddressBySurname should return contact info")
    void findPhoneAndAddressBySurname_existingEmployee_shouldReturnContact() {
        EmployeeContactDto contactDto = new EmployeeContactDto();
        contactDto.setPhone_number("+380501234567");
        contactDto.setCity("Київ");
        contactDto.setStreet("вул. Хрещатик, 1");
        contactDto.setZip_code("01001");
        Pageable pageable = Pageable.ofSize(10);
        String surname = "Іваненко";
        PageResponseDto<EmployeeContactDto> expectedPage = PageResponseDto.of(
                List.of(contactDto),
                10,
                1L,
                false
        );
        when(employeeRepository.findPhoneAndAddressBySurname(surname, pageable, null))
                .thenReturn(expectedPage);
        PageResponseDto<EmployeeContactDto> result = service
                .findPhoneAndAddressBySurname(surname, pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("+380501234567", result.getContent().get(0).getPhone_number());
        assertEquals("Київ", result.getContent().get(0).getCity());
        verify(employeeRepository, times(1))
                .findPhoneAndAddressBySurname(surname, pageable, null);
    }
}
