package org.example.mapper.employee;

import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(EmployeeMapper.class);
    }

    @Test
    void shouldMapDtoToEntity() {
        EmployeeRegistrationRequestDto dto = new EmployeeRegistrationRequestDto();
        dto.setId_employee("EMP001");
        dto.setEmpl_surname("Smith");
        dto.setPassword("pass123");
        dto.setRole("CASHIER");

        Employee employee = mapper.toEmployeeEntity(dto);

        assertNotNull(employee);
        assertEquals("EMP001", employee.getId_employee());
        assertEquals("Smith", employee.getEmpl_surname());
        assertEquals("pass123", employee.getPassword());
        assertNotNull(employee.getRole());
        assertEquals(Role.RoleName.CASHIER, employee.getRole().getName());
    }

    @Test
    void shouldMapEntityToResponseDto() {
        Role role = new Role();
        role.setName(Role.RoleName.MANAGER);

        Employee employee = new Employee();
        employee.setId_employee("EMP002");
        employee.setEmpl_surname("Brown");
        employee.setRole(role);

        EmployeeResponseDto responseDto = mapper.toEmployeeResponseDto(employee);

        assertNotNull(responseDto);
        assertEquals("EMP002", responseDto.getId_employee());
        assertEquals("Brown", responseDto.getEmpl_surname());
        assertEquals("MANAGER", responseDto.getRole());
    }

    @Test
    void shouldMapStringToRole() {
        Role role = mapper.map("CASHIER");
        assertNotNull(role);
        assertEquals(Role.RoleName.CASHIER, role.getName());
    }

    @Test
    void shouldMapRoleToString() {
        Role role = new Role();
        role.setName(Role.RoleName.MANAGER);

        String roleString = mapper.map(role);
        assertEquals("MANAGER", roleString);
    }

    @Test
    void shouldReturnNullForNullString() {
        assertNull(mapper.map((String) null));
    }

    @Test
    void shouldReturnNullForNullRole() {
        assertNull(mapper.map((Role) null));
    }

    @Test
    void shouldThrowExceptionForInvalidRoleString() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> mapper.map("INVALID_ROLE"));
        assertTrue(exception.getMessage().contains("Invalid role name"));
    }
}
