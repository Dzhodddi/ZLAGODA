package org.example.repository.employee;

import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.mapper.employee.EmployeeMapper;
import org.example.mapper.employee.EmployeeRowMapper;
import org.example.model.employee.Employee;
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
        List<Employee> res = jdbcTemplate.query("SELECT * FROM employee", employeeRowMapper);
        return res.stream()
                .map(employeeMapper::toEmployeeResponseDto)
                .toList();
    }

    public EmployeeResponseDto save(Employee employee) {
        String sql = """
            INSERT INTO employee (
                id_employee, empl_surname, empl_name, empl_patronymic,
                empl_role,
                date_of_birth, date_of_start, phone_number,
                empl_salary, city, street, zip_code, password
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                employee.getIdEmployee(),
                employee.getEmplSurname(),
                employee.getEmplName(),
                employee.getEmplPatronymic(),
                employee.getRole().getName().name(),
                employee.getDateOfBirth(),
                employee.getDateOfStart(),
                employee.getPhoneNumber(),
                employee.getSalary(),
                employee.getCity(),
                employee.getStreet(),
                employee.getZipCode(),
                employee.getPassword()
        );
        return employeeMapper.toEmployeeResponseDto(employee);
    }

    public EmployeeResponseDto updateEmployeeById(String id, EmployeeRegistrationRequestDto employee) {
        String sql = """
            UPDATE employee SET
                empl_surname = ?, empl_name = ?, empl_patronymic = ?,
                empl_role = ?,
                date_of_birth = ?, date_of_start = ?,
                phone_number = ?, empl_salary = ?, city = ?, 
                street = ?, zip_code = ?, password = ?
            WHERE id_employee = ?
        """;
        jdbcTemplate.update(sql,
                employee.getEmplSurname(),
                employee.getEmplName(),
                employee.getEmplPatronymic(),
                employee.getRole(),
                employee.getDateOfBirth(),
                employee.getDateOfStart(),
                employee.getPhoneNumber(),
                employee.getSalary(),
                employee.getCity(),
                employee.getStreet(),
                employee.getZipCode(),
                employee.getPassword(),
                id
        );

        return findByIdEmployee(id)
                .map(employeeMapper::toEmployeeResponseDto)
                .orElseThrow();
    }

    public void deleteEmployeeById(String id) {
        jdbcTemplate.update("DELETE FROM employee WHERE id_employee = ?", id);
    }

    public boolean existsByIdEmployee(String idEmployee) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employee WHERE id_employee = ?", Integer.class, idEmployee);
        return count > 0;
    }

    public Optional<Employee> findByIdEmployee(String id) {
        String sql = "SELECT * FROM employee WHERE id_employee = ?";
        try {
            Employee employee = jdbcTemplate.queryForObject(sql, employeeRowMapper, id);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}