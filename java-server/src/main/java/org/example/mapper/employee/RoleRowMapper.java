package org.example.mapper.employee;

import org.example.model.employee.Role;
import org.example.model.employee.Role.RoleName;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        String roleName = rs.getString("name");
        if (roleName != null) {
            role.setName(RoleName.valueOf(roleName.toUpperCase()));
        }
        return role;
    }
}

