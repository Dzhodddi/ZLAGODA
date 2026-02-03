package org.example.repository.employee;

import lombok.RequiredArgsConstructor;
import org.example.mapper.employee.RoleRowMapper;
import org.example.model.employee.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RoleRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RoleRowMapper roleRowMapper;

    public Optional<Role> findRoleByName(Role.RoleName name) {
        try {
            Role role = jdbcTemplate.queryForObject(
                    """
                    SELECT *
                    FROM roles
                    WHERE name = ?
                    """,
                    roleRowMapper,
                    name.name()
            );
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(Role role) {
        jdbcTemplate.update("""
                            INSERT INTO roles (name) VALUES (?)
                            """,
                role.getName().name());
    }
}
