package org.example.mapper.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Row Mapper Tests")
class EmployeeRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private EmployeeRowMapper mapper;

    private Date dateOfBirth;
    private Date dateOfStart;

    @BeforeEach
    void setUp() {
        dateOfBirth = Date.valueOf("1990-01-01");
        dateOfStart = Date.valueOf("2020-01-01");
    }

    @Test
    @DisplayName("mapRow should map all employee fields correctly")
    void mapRow_allFields_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Іваненко");
        when(resultSet.getString("empl_name")).thenReturn("Іван");
        when(resultSet.getString("empl_patronymic")).thenReturn("Іванович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("15000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Хрещатик 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("encodedPassword");
        when(resultSet.getString("empl_role")).thenReturn("MANAGER");

        Employee result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals("EMP001", result.getId_employee());
        assertEquals("Іваненко", result.getEmpl_surname());
        assertEquals("Іван", result.getEmpl_name());
        assertEquals("Іванович", result.getEmpl_patronymic());
        assertEquals(new BigDecimal("15000.00"), result.getSalary());
        assertEquals(dateOfBirth, result.getDate_of_birth());
        assertEquals(dateOfStart, result.getDate_of_start());
        assertEquals("+380501234567", result.getPhone_number());
        assertEquals("Київ", result.getCity());
        assertEquals("Хрещатик 1", result.getStreet());
        assertEquals("01001", result.getZip_code());
        assertEquals("encodedPassword", result.getPassword());
        assertNotNull(result.getRole());
        assertEquals(Role.RoleName.MANAGER, result.getRole().getName());
    }

    @Test
    @DisplayName("mapRow should map CASHIER role correctly")
    void mapRow_cashierRole_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP002");
        when(resultSet.getString("empl_surname")).thenReturn("Петренко");
        when(resultSet.getString("empl_name")).thenReturn("Петро");
        when(resultSet.getString("empl_patronymic")).thenReturn("Петрович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("12000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380509876543");
        when(resultSet.getString("city")).thenReturn("Львів");
        when(resultSet.getString("street")).thenReturn("Проспект Свободи 1");
        when(resultSet.getString("zip_code")).thenReturn("79000");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn("CASHIER");

        Employee result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals("EMP002", result.getId_employee());
        assertNotNull(result.getRole());
        assertEquals(Role.RoleName.CASHIER, result.getRole().getName());
    }

    @Test
    @DisplayName("mapRow should handle null role correctly")
    void mapRow_nullRole_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Іваненко");
        when(resultSet.getString("empl_name")).thenReturn("Іван");
        when(resultSet.getString("empl_patronymic")).thenReturn("Іванович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("15000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Хрещатик 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn(null);

        Employee result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertNull(result.getRole());
    }

    @Test
    @DisplayName("mapRow should throw SQLException for invalid role")
    void mapRow_invalidRole_shouldThrowException() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Іваненко");
        when(resultSet.getString("empl_name")).thenReturn("Іван");
        when(resultSet.getString("empl_patronymic")).thenReturn("Іванович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("15000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Хрещатик 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn("INVALID_ROLE");

        SQLException exception = assertThrows(SQLException.class,
                () -> mapper.mapRow(resultSet, 0));

        assertTrue(exception.getMessage().contains("Invalid role name in DB"));
        assertTrue(exception.getMessage().contains("INVALID_ROLE"));
    }

    @Test
    @DisplayName("mapRow should handle null patronymic")
    void mapRow_nullPatronymic_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Smith");
        when(resultSet.getString("empl_name")).thenReturn("John");
        when(resultSet.getString("empl_patronymic")).thenReturn(null);
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("15000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Main St 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn("MANAGER");

        Employee result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertNull(result.getEmpl_patronymic());
        assertEquals("John", result.getEmpl_name());
    }

    @Test
    @DisplayName("mapRow should work with different row numbers")
    void mapRow_differentRowNumbers_shouldWork() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Іваненко");
        when(resultSet.getString("empl_name")).thenReturn("Іван");
        when(resultSet.getString("empl_patronymic")).thenReturn("Іванович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(new BigDecimal("15000.00"));
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Хрещатик 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn("MANAGER");

        Employee result1 = mapper.mapRow(resultSet, 0);
        Employee result2 = mapper.mapRow(resultSet, 5);
        Employee result3 = mapper.mapRow(resultSet, 100);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals("EMP001", result1.getId_employee());
        assertEquals("EMP001", result2.getId_employee());
        assertEquals("EMP001", result3.getId_employee());
    }

    @Test
    @DisplayName("mapRow should handle zero salary")
    void mapRow_zeroSalary_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("id_employee")).thenReturn("EMP001");
        when(resultSet.getString("empl_surname")).thenReturn("Іваненко");
        when(resultSet.getString("empl_name")).thenReturn("Іван");
        when(resultSet.getString("empl_patronymic")).thenReturn("Іванович");
        when(resultSet.getBigDecimal("empl_salary")).thenReturn(BigDecimal.ZERO);
        when(resultSet.getDate("date_of_birth")).thenReturn(dateOfBirth);
        when(resultSet.getDate("date_of_start")).thenReturn(dateOfStart);
        when(resultSet.getString("phone_number")).thenReturn("+380501234567");
        when(resultSet.getString("city")).thenReturn("Київ");
        when(resultSet.getString("street")).thenReturn("Хрещатик 1");
        when(resultSet.getString("zip_code")).thenReturn("01001");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getString("empl_role")).thenReturn("CASHIER");

        Employee result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getSalary());
    }
}
