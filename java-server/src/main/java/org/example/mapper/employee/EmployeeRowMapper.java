package org.example.mapper.employee;

import org.example.model.employee.Employee;
import org.example.model.employee.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();
        employee.setIdEmployee(rs.getString("id_employee"));
        employee.setEmplSurname(rs.getString("empl_surname"));
        employee.setEmplName(rs.getString("empl_name"));
        employee.setEmplPatronymic(rs.getString("empl_patronymic"));
        employee.setSalary(rs.getBigDecimal("empl_salary"));
        employee.setDateOfBirth(rs.getDate("date_of_birth"));
        employee.setDateOfStart(rs.getDate("date_of_start"));
        employee.setPhoneNumber(rs.getString("phone_number"));
        employee.setCity(rs.getString("city"));
        employee.setStreet(rs.getString("street"));
        employee.setZipCode(rs.getString("zip_code"));
        employee.setPassword(rs.getString("password"));
        String roleNameStr = rs.getString("empl_role");
        if (roleNameStr != null) {
            Role role = new Role();
            try {
                role.setName(Role.RoleName.valueOf(roleNameStr));
                employee.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new SQLException("Invalid role name: " + roleNameStr);
            }
        }

        return employee;
    }
}
