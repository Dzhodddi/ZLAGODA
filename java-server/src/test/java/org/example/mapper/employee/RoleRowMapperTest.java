package org.example.mapper.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.employee.Role;
import org.example.model.employee.Role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Role Row Mapper Tests")
class RoleRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private RoleRowMapper mapper;

    @Test
    @DisplayName("mapRow should map MANAGER role correctly")
    void mapRow_managerRole_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("name")).thenReturn("MANAGER");

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(RoleName.MANAGER, result.getName());
    }

    @Test
    @DisplayName("mapRow should map CASHIER role correctly")
    void mapRow_cashierRole_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("name")).thenReturn("CASHIER");

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(RoleName.CASHIER, result.getName());
    }

    @Test
    @DisplayName("mapRow should convert lowercase to uppercase")
    void mapRow_lowercaseRole_shouldConvertToUppercase() throws SQLException {
        when(resultSet.getString("name")).thenReturn("manager");

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(RoleName.MANAGER, result.getName());
    }

    @Test
    @DisplayName("mapRow should convert mixed case to uppercase")
    void mapRow_mixedCaseRole_shouldConvertToUppercase() throws SQLException {
        when(resultSet.getString("name")).thenReturn("CaShIeR");

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(RoleName.CASHIER, result.getName());
    }

    @Test
    @DisplayName("mapRow should handle null role name")
    void mapRow_nullRoleName_shouldCreateRoleWithNullName() throws SQLException {
        when(resultSet.getString("name")).thenReturn(null);

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertNull(result.getName());
    }

    @Test
    @DisplayName("mapRow should throw IllegalArgumentException for invalid role name")
    void mapRow_invalidRoleName_shouldThrowException() throws SQLException {
        when(resultSet.getString("name")).thenReturn("INVALID_ROLE");

        assertThrows(IllegalArgumentException.class, () -> mapper.mapRow(resultSet, 0));
    }

    @Test
    @DisplayName("mapRow should work with different row numbers")
    void mapRow_differentRowNumbers_shouldWork() throws SQLException {
        when(resultSet.getString("name")).thenReturn("MANAGER");

        Role result1 = mapper.mapRow(resultSet, 0);
        Role result2 = mapper.mapRow(resultSet, 5);
        Role result3 = mapper.mapRow(resultSet, 100);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(RoleName.MANAGER, result1.getName());
        assertEquals(RoleName.MANAGER, result2.getName());
        assertEquals(RoleName.MANAGER, result3.getName());
    }

    @Test
    @DisplayName("mapRow should handle role name with extra spaces")
    void mapRow_roleNameWithSpaces_shouldTrim() throws SQLException {
        when(resultSet.getString("name")).thenReturn("  MANAGER  ");

        Role result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(RoleName.MANAGER, result.getName());
    }

    @Test
    @DisplayName("mapRow should create new Role instance each time")
    void mapRow_multipleCalls_shouldCreateNewInstances() throws SQLException {
        when(resultSet.getString("name")).thenReturn("MANAGER");

        Role result1 = mapper.mapRow(resultSet, 0);
        Role result2 = mapper.mapRow(resultSet, 1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getName(), result2.getName());
    }
}
