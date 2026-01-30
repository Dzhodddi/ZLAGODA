package org.example.repository.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.mapper.employee.RoleRowMapper;
import org.example.model.employee.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Role Repository Tests")
class RoleRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RoleRowMapper roleRowMapper;

    @InjectMocks
    private RoleRepository repository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(Role.RoleName.MANAGER);
    }

    @Test
    @DisplayName("findRoleByName should return role when role exists")
    void findRoleByName_existingRole_shouldReturnRole() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("MANAGER")
        )).thenReturn(role);

        Optional<Role> result = repository.findRoleByName(Role.RoleName.MANAGER);

        assertTrue(result.isPresent());
        assertEquals(Role.RoleName.MANAGER, result.get().getName());
        verify(jdbcTemplate, times(1)).queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("MANAGER")
        );
    }

    @Test
    @DisplayName("findRoleByName should return empty when role does not exist")
    void findRoleByName_nonExistingRole_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("CASHIER")
        )).thenThrow(EmptyResultDataAccessException.class);

        Optional<Role> result = repository.findRoleByName(Role.RoleName.CASHIER);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("CASHIER")
        );
    }

    @Test
    @DisplayName("findRoleByName should work with CASHIER role")
    void findRoleByName_cashierRole_shouldReturnRole() {
        Role cashierRole = new Role();
        cashierRole.setName(Role.RoleName.CASHIER);

        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("CASHIER")
        )).thenReturn(cashierRole);

        Optional<Role> result = repository.findRoleByName(Role.RoleName.CASHIER);

        assertTrue(result.isPresent());
        assertEquals(Role.RoleName.CASHIER, result.get().getName());
    }

    @Test
    @DisplayName("save should insert new role")
    void save_newRole_shouldInsertRole() {
        when(jdbcTemplate.update(anyString(), eq("MANAGER")))
                .thenReturn(1);

        repository.save(role);

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("MANAGER")
        );
    }

    @Test
    @DisplayName("save should insert CASHIER role")
    void save_cashierRole_shouldInsertRole() {
        Role cashierRole = new Role();
        cashierRole.setName(Role.RoleName.CASHIER);

        when(jdbcTemplate.update(anyString(), eq("CASHIER")))
                .thenReturn(1);

        repository.save(cashierRole);

        verify(jdbcTemplate, times(1)).update(
                anyString(),
                eq("CASHIER")
        );
    }

    @Test
    @DisplayName("findRoleByName should handle null return from query")
    void findRoleByName_nullResult_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("MANAGER")
        )).thenReturn(null);

        Optional<Role> result = repository.findRoleByName(Role.RoleName.MANAGER);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save should use correct SQL query")
    void save_shouldUseCorrectSQL() {
        repository.save(role);

        verify(jdbcTemplate).update(
                anyString(),
                eq("MANAGER")
        );
    }

    @Test
    @DisplayName("findRoleByName should query with correct SQL")
    void findRoleByName_shouldUseCorrectSQL() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("MANAGER")
        )).thenReturn(role);

        repository.findRoleByName(Role.RoleName.MANAGER);

        verify(jdbcTemplate).queryForObject(
                anyString(),
                eq(roleRowMapper),
                eq("MANAGER")
        );
    }
}
