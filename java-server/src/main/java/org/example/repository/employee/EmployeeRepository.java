package org.example.repository.employee;

import lombok.RequiredArgsConstructor;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.employee.EmployeeMapper;
import org.example.mapper.employee.EmployeeRowMapper;
import org.example.model.employee.Employee;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRowMapper employeeRowMapper;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeResponseDto> findAll() {
        return jdbcTemplate.query("SELECT * FROM employee", employeeRowMapper)
                .stream()
                .map(employeeMapper::toEmployeeResponseDto)
                .toList();
    }

    public EmployeeResponseDto save(Employee employee) {
        try {
            Employee saved = jdbcTemplate.queryForObject(
                    """
                    INSERT INTO employee (
                        id_employee, empl_surname, empl_name, empl_patronymic,
                        empl_role, empl_salary,
                        date_of_birth, date_of_start,
                        phone_number, city, street, zip_code, password
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    RETURNING *
                    """,
                    employeeRowMapper,
                    employee.getId_employee(),
                    employee.getEmpl_surname(),
                    employee.getEmpl_name(),
                    employee.getEmpl_patronymic(),
                    employee.getRole().getName().name(),
                    employee.getSalary(),
                    employee.getDate_of_birth(),
                    employee.getDate_of_start(),
                    employee.getPhone_number(),
                    employee.getCity(),
                    employee.getStreet(),
                    employee.getZip_code(),
                    employee.getPassword()
            );

            return employeeMapper.toEmployeeResponseDto(saved);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid role or data integrity violation", e);
        }
    }

    public EmployeeResponseDto updateEmployeeById(String id,
                                                  EmployeeUpdateRequestDto requestDto) {
        if (!existsByIdEmployee(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }

        try {
            int updated = jdbcTemplate.update(
                    """
                    UPDATE employee SET
                        empl_surname = ?,
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
                    requestDto.getRole(),
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
            throw new IllegalArgumentException("Invalid role or data integrity violation", e);
        }
    }

    public void deleteEmployeeById(String id) {
        if (!existsByIdEmployee(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        jdbcTemplate.update("DELETE FROM employee WHERE id_employee = ?", id);
    }

    public boolean existsByIdEmployee(String idEmployee) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employee WHERE id_employee = ?",
                Integer.class,
                idEmployee
        );
        return count != null && count > 0;
    }

    public Optional<Employee> findByIdEmployee(String id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM employee WHERE id_employee = ?",
                            employeeRowMapper,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
