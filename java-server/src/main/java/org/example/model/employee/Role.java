package org.example.model.employee;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class Role implements GrantedAuthority {
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleName {
        Manager, Cashier
    }
}
