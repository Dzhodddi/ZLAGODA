package org.example.repository.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.page.PageResponseDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidRoleException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.mapper.employee.EmployeeRowMapper;
import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Repository Tests")
class EmployeeRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private EmployeeRowMapper employeeRowMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeRepository repository;

    private Employee employee;
    private EmployeeResponseDto employeeResponseDto;
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
        employee.setPassword("password123");

        employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setId_employee("EMP001");
        employeeResponseDto.setEmpl_surname("Іваненко");
        employeeResponseDto.setEmpl_name("Іван");

        updateRequestDto = new EmployeeUpdateRequestDto();
        updateRequestDto.setEmpl_surname("Петренко");
        updateRequestDto.setEmpl_name("Петро");
        updateRequestDto.setEmpl_patronymic("Петрович");
        updateRequestDto.setRole("MANAGER");
        updateRequestDto.setSalary(new BigDecimal("16000.00"));
        updateRequestDto.setDate_of_birth(Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        updateRequestDto.setDate_of_start(Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        updateRequestDto.setPhone_number("+380501234567");
        updateRequestDto.setCity("Київ");
        updateRequestDto.setStreet("Хрещатик 1");
        updateRequestDto.setZip_code("01001");
    }

    @Test
    @DisplayName("findAll should return list of employees sorted by surname")
    void findAll_shouldReturnEmployeesSortedBySurname() {
        when(jdbcTemplate.query(anyString(), any(EmployeeRowMapper.class), anyInt()))
                .thenReturn(List.of(employee));
        when(employeeMapper.toEmployeeResponseDto(employee))
                .thenReturn(employeeResponseDto);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);
        Pageable pageable = Pageable.ofSize(10);
        PageResponseDto<EmployeeResponseDto> result = repository
                .findAllCashiers(pageable, null);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Іваненко", result.getContent().get(0).getEmpl_surname());
    }

    @Test
    @DisplayName("save should insert new employee and return EmployeeResponseDto")
    void save_newEmployee_shouldReturnResponseDto() {
        when(jdbcTemplate.update(
                contains("INSERT INTO employee"),
                eq("EMP001"),
                eq("Іваненко"),
                eq("Іван"),
                eq("Іванович"),
                eq("MANAGER"),
                eq(new BigDecimal("15000.00")),
                any(Date.class),
                any(Date.class),
                eq("+380501234567"),
                eq("Київ"),
                eq("Хрещатик 1"),
                eq("01001"),
                eq("password123")))
                .thenReturn(1);

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(employeeRowMapper),
                eq("EMP001")))
                .thenReturn(employee);

        when(employeeMapper.toEmployeeResponseDto(employee))
                .thenReturn(employeeResponseDto);

        EmployeeResponseDto result = repository.save(employee);

        assertNotNull(result);
        assertEquals("EMP001", result.getId_employee());

        verify(jdbcTemplate, times(1)).update(
                contains("INSERT INTO employee"),
                eq("EMP001"),
                eq("Іваненко"),
                eq("Іван"),
                eq("Іванович"),
                eq("MANAGER"),
                eq(new BigDecimal("15000.00")),
                any(Date.class),
                any(Date.class),
                eq("+380501234567"),
                eq("Київ"),
                eq("Хрещатик 1"),
                eq("01001"),
                eq("password123")
        );
    }

    @Test
    @DisplayName("save should throw InvalidRoleException on data integrity violation")
    void save_invalidRole_shouldThrowException() {
        when(jdbcTemplate.update(
                contains("INSERT INTO employee"),
                eq("EMP001"),
                eq("Іваненко"),
                eq("Іван"),
                eq("Іванович"),
                eq("MANAGER"),
                eq(new BigDecimal("15000.00")),
                any(Date.class),
                any(Date.class),
                eq("+380501234567"),
                eq("Київ"),
                eq("Хрещатик 1"),
                eq("01001"),
                eq("password123")))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidRoleException.class, () -> repository.save(employee));
    }

    @Test
    @DisplayName("updateEmployeeById should update employee when exists")
    void updateEmployeeById_existingEmployee_shouldReturnUpdatedDto() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), eq("EMP001")))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(employeeRowMapper), eq("EMP001")))
                .thenReturn(employee);
        when(employeeMapper.toEmployeeResponseDto(employee))
                .thenReturn(employeeResponseDto);

        EmployeeResponseDto result = repository.updateEmployeeById("EMP001", updateRequestDto);

        assertNotNull(result);
        verify(jdbcTemplate, times(1)).update(
                anyString(),
                anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), eq("EMP001")
        );
    }

    @Test
    @DisplayName("updateEmployeeById should throw EntityNotFoundException when employee does not exist")
    void updateEmployeeById_nonExistingEmployee_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP999")))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateEmployeeById("EMP999", updateRequestDto));
    }

    @Test
    @DisplayName("updateEmployeeById should throw InvalidRoleException on data integrity violation")
    void updateEmployeeById_invalidRole_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), eq("EMP001")))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidRoleException.class,
                () -> repository.updateEmployeeById("EMP001", updateRequestDto));
    }

    @Test
    @DisplayName("deleteEmployeeById should delete employee when exists")
    void deleteEmployeeById_existingEmployee_shouldDeleteEmployee() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), eq("EMP001")))
                .thenReturn(1);

        repository.deleteEmployeeById("EMP001");

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("EMP001")
        );
    }

    @Test
    @DisplayName("deleteEmployeeById should throw EntityNotFoundException when employee does not exist")
    void deleteEmployeeById_nonExistingEmployee_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP999")))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.deleteEmployeeById("EMP999"));
    }

    @Test
    @DisplayName("existsByIdEmployee should return true when employee exists")
    void existsByIdEmployee_existingEmployee_shouldReturnTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(1);

        assertTrue(repository.existsByIdEmployee("EMP001"));
    }

    @Test
    @DisplayName("existsByIdEmployee should return false when employee does not exist")
    void existsByIdEmployee_nonExistingEmployee_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP999")))
                .thenReturn(0);

        assertFalse(repository.existsByIdEmployee("EMP999"));
    }

    @Test
    @DisplayName("findByIdEmployee should return employee when exists")
    void findByIdEmployee_existingEmployee_shouldReturnEmployee() {
        when(jdbcTemplate.queryForObject(anyString(), eq(employeeRowMapper), eq("EMP001")))
                .thenReturn(employee);

        Optional<Employee> result = repository.findByIdEmployee("EMP001");

        assertTrue(result.isPresent());
        assertEquals("EMP001", result.get().getId_employee());
        assertEquals("Іваненко", result.get().getEmpl_surname());
    }

    @Test
    @DisplayName("findByIdEmployee should return empty when employee does not exist")
    void findByIdEmployee_nonExistingEmployee_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), eq(employeeRowMapper), eq("EMP999")))
                .thenThrow(EmptyResultDataAccessException.class);
        Optional<Employee> result = repository.findByIdEmployee("EMP999");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAllCashiers should return only cashiers sorted by surname")
    void findAllCashiers_shouldReturnCashiers() {
        Role cashierRole = new Role();
        cashierRole.setName(Role.RoleName.CASHIER);
        employee.setRole(cashierRole);

        when(jdbcTemplate.query(anyString(), any(EmployeeRowMapper.class), anyInt()))
                .thenReturn(List.of(employee));
        when(employeeMapper.toEmployeeResponseDto(employee))
                .thenReturn(employeeResponseDto);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);

        Pageable pageable = Pageable.ofSize(10);
        PageResponseDto<EmployeeResponseDto> result = repository
                .findAll(pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Іваненко", result.getContent().get(0).getEmpl_surname());
    }

    @Test
    @DisplayName("findPhoneAndAddressBySurname should return contact info when employee exists")
    void findPhoneAndAddressBySurname_existingEmployee_shouldReturnContactInfo() {
        String surname = "Іваненко";
        Pageable pageable = PageRequest.of(0, 10);

        EmployeeContactDto dto = new EmployeeContactDto();
        dto.setPhone_number("+380501234567");
        dto.setCity("Київ");
        dto.setStreet("Хрещатик 1");
        dto.setZip_code("01001");

        when(jdbcTemplate.query(
                anyString(),
                any(org.springframework.jdbc.core.RowMapper.class),
                eq(surname),
                eq(pageable.getPageSize())
        )).thenReturn(List.of(dto));

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(surname)
        )).thenReturn(1);

        PageResponseDto<EmployeeContactDto> result = repository.findPhoneAndAddressBySurname(
                surname, pageable, null);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        EmployeeContactDto resultDto = result.getContent().get(0);
        assertEquals("+380501234567", resultDto.getPhone_number());
        assertEquals("Київ", resultDto.getCity());
        assertEquals("Хрещатик 1", resultDto.getStreet());
        assertEquals("01001", resultDto.getZip_code());
    }

    @Test
    @DisplayName("findPhoneAndAddressBySurname should return empty page when employee does not exist")
    void findPhoneAndAddressBySurname_nonExistingEmployee_shouldReturnEmptyPage() {
        // Arrange
        String surname = "Nonexistent";
        Pageable pageable = PageRequest.of(0, 10);

        when(jdbcTemplate.query(
                anyString(),
                any(org.springframework.jdbc.core.RowMapper.class),
                eq(surname),
                eq(pageable.getPageSize())
        )).thenReturn(List.of());

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(surname)
        )).thenReturn(0);

        PageResponseDto<EmployeeContactDto> result = repository.findPhoneAndAddressBySurname(
                surname, pageable, null);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertFalse(result.isHasNext());
    }

    @Test
    @DisplayName("findPhoneAndAddressBySurname with pagination should return correct page")
    void findPhoneAndAddressBySurname_withPagination_shouldReturnCorrectPage() {
        String surname = "Іваненко";
        String lastSeenId = "EMP001";
        Pageable pageable = PageRequest.of(0, 10);
        EmployeeContactDto dto1 = new EmployeeContactDto();
        dto1.setPhone_number("+380501234567");
        dto1.setCity("Київ");
        dto1.setStreet("Хрещатик 1");
        dto1.setZip_code("01001");
        EmployeeContactDto dto2 = new EmployeeContactDto();
        dto2.setPhone_number("+380507654321");
        dto2.setCity("Львів");
        dto2.setStreet("Проспект Свободи 2");
        dto2.setZip_code("79000");
        when(jdbcTemplate.query(
                anyString(),
                any(org.springframework.jdbc.core.RowMapper.class),
                eq(surname),
                eq(lastSeenId),
                eq(pageable.getPageSize())
        )).thenReturn(List.of(dto1, dto2));
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(surname)
        )).thenReturn(2);

        PageResponseDto<EmployeeContactDto> result = repository.findPhoneAndAddressBySurname(
                surname, pageable, lastSeenId);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("updateEmployeeById should throw EntityNotFoundException when update affects 0 rows")
    void updateEmployeeById_updateFailed_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), eq("EMP001")))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateEmployeeById("EMP001", updateRequestDto));
    }

    @Test
    @DisplayName("existsByIdEmployee should handle null count")
    void existsByIdEmployee_nullCount_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("EMP001")))
                .thenReturn(null);

        assertFalse(repository.existsByIdEmployee("EMP001"));
    }
}
