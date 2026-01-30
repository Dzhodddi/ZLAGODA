package org.example.repository.employee;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRowMapper employeeRowMapper;
    private final EmployeeMapper employeeMapper;

    public PageResponseDto<EmployeeResponseDto> findAll(Pageable pageable,
                                                        String lastSeenSurname,
                                                        String lastSeenId) {
        List<EmployeeResponseDto> employees;
        if (lastSeenSurname == null || lastSeenId == null) {
            employees = jdbcTemplate.query(
                            """
                            SELECT *
                            FROM employee
                            ORDER BY empl_surname, id_employee
                            FETCH FIRST ? ROWS ONLY
                            """,
                            employeeRowMapper,
                            pageable.getPageSize()
                    ).stream()
                    .map(employeeMapper::toEmployeeResponseDto)
                    .toList();
        } else {
            employees = jdbcTemplate.query(
                            """
                            SELECT *
                            FROM employee
                            WHERE (empl_surname > ?)
                               OR (empl_surname = ? AND id_employee > ?)
                            ORDER BY empl_surname, id_employee
                            FETCH FIRST ? ROWS ONLY
                            """,
                            employeeRowMapper,
                            lastSeenSurname,
                            lastSeenSurname,
                            lastSeenId,
                            pageable.getPageSize()
                    ).stream()
                    .map(employeeMapper::toEmployeeResponseDto)
                    .toList();
        }
        long total = getTotalCount();
        return PageResponseDto.of(
                employees,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );
    }

    public List<EmployeeResponseDto> findAllNoPagination() {
        return jdbcTemplate.query("""
                        SELECT *
                        FROM employee
                        ORDER BY empl_surname
                        """,
                        employeeRowMapper)
                .stream()
                .map(employeeMapper::toEmployeeResponseDto)
                .toList();
    }

    public EmployeeResponseDto save(Employee employee) {
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO employee (
                        id_employee, empl_surname, empl_name, empl_patronymic,
                        empl_role, empl_salary,
                        date_of_birth, date_of_start,
                        phone_number, city, street, zip_code, password
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    employee.getId_employee(),
                    employee.getEmpl_surname(),
                    employee.getEmpl_name(),
                    employee.getEmpl_patronymic(),
                    employee.getRole().getName().name().toUpperCase().trim(),
                    employee.getSalary(),
                    employee.getDate_of_birth(),
                    employee.getDate_of_start(),
                    employee.getPhone_number(),
                    employee.getCity(),
                    employee.getStreet(),
                    employee.getZip_code(),
                    employee.getPassword()
            );

            return findByIdEmployee(employee.getId_employee())
                    .map(employeeMapper::toEmployeeResponseDto)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Employee not found after saving: " + employee.getId_employee()));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidRoleException("Invalid role name: " + employee.getRole().getName());
        }
    }

    public EmployeeResponseDto updateEmployeeById(String id,
                                                  EmployeeUpdateRequestDto requestDto) {
        if (!existsByIdEmployee(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }

        try {
            Role.RoleName.valueOf(requestDto.getRole().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role name: " + requestDto.getRole()
                    + ". Allowed values: MANAGER, CASHIER");
        }

        try {
            int updated = jdbcTemplate.update(
                    """
                    UPDATE employee
                    SET empl_surname = ?,
                        empl_name = ?,
                        empl_patronymic = ?,
                        empl_role = ?,
                        empl_salary = ?,
                        date_of_birth = ?,
                        date_of_start = ?,
                        phone_number = ?,
                        city = ?,
                        street = ?,
                        zip_code = ?
                    WHERE id_employee = ?
                    """,
                    requestDto.getEmpl_surname(),
                    requestDto.getEmpl_name(),
                    requestDto.getEmpl_patronymic(),
                    requestDto.getRole().toUpperCase().trim(),
                    requestDto.getSalary(),
                    requestDto.getDate_of_birth(),
                    requestDto.getDate_of_start(),
                    requestDto.getPhone_number(),
                    requestDto.getCity(),
                    requestDto.getStreet(),
                    requestDto.getZip_code(),
                    id
            );

            if (updated == 0) {
                throw new EntityNotFoundException("Update failed, employee not found: " + id);
            }

            return findByIdEmployee(id)
                    .map(employeeMapper::toEmployeeResponseDto)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found after update: " + id));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidRoleException("Invalid role name: " + requestDto.getRole());
        }
    }

    public void deleteEmployeeById(String id) {
        if (!existsByIdEmployee(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        jdbcTemplate.update("""
                                DELETE
                                FROM employee
                                WHERE id_employee = ?
                                """, id);
    }

    public boolean existsByIdEmployee(String idEmployee) {
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM employee
                    WHERE id_employee = ?
                    """,
                Integer.class,
                idEmployee
        );
        return count != null && count > 0;
    }

    public Optional<Employee> findByIdEmployee(String id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT *
                            FROM employee
                            WHERE id_employee = ?
                            """,
                            employeeRowMapper,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public PageResponseDto<EmployeeResponseDto> findAllCashiers(Pageable pageable,
                                                     String lastSeenSurname,
                                                     String lastSeenId) {
        List<EmployeeResponseDto> employees;
        if (lastSeenSurname == null || lastSeenId == null) {
            employees = jdbcTemplate.query(
                            """
                            SELECT *
                            FROM employee
                            WHERE empl_role = 'CASHIER'
                            ORDER BY empl_surname, id_employee
                            FETCH FIRST ? ROWS ONLY
                            """,
                            employeeRowMapper,
                            pageable.getPageSize()
                    ).stream()
                    .map(employeeMapper::toEmployeeResponseDto)
                    .toList();
        } else {
            employees = jdbcTemplate.query(
                            """
                            SELECT *
                            FROM employee
                            WHERE empl_role = 'CASHIER'
                              AND ((empl_surname > ?)
                                OR (empl_surname = ? AND id_employee > ?))
                            ORDER BY empl_surname, id_employee
                            FETCH FIRST ? ROWS ONLY
                            """,
                            employeeRowMapper,
                            lastSeenSurname,
                            lastSeenSurname,
                            lastSeenId,
                            pageable.getPageSize()
                    ).stream()
                    .map(employeeMapper::toEmployeeResponseDto)
                    .toList();
        }
        long total = getCashierCount();
        return PageResponseDto.of(
                employees,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );
    }

    public Optional<EmployeeContactDto> findPhoneAndAddressBySurname(String surname) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT phone_number, city, street, zip_code
                            FROM employee
                            WHERE empl_surname = ?
                            """,
                            (rs, rowNum) -> {
                                EmployeeContactDto dto = new EmployeeContactDto();
                                dto.setPhone_number(rs.getString("phone_number"));
                                dto.setCity(rs.getString("city"));
                                dto.setStreet(rs.getString("street"));
                                dto.setZip_code(rs.getString("zip_code"));
                                return dto;
                            },
                            surname
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private long getTotalCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM employee
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }

    private long getCashierCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM employee
                WHERE empl_role = 'CASHIER'
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }
}
