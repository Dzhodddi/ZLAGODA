package org.example.mapper.employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.employee.Role;
import org.example.model.employee.Role.RoleName;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        String roleName = rs.getString("name");
        if (roleName != null) {
            role.setName(RoleName.valueOf(roleName.trim().toUpperCase()));
        }
        return role;
    }
}
