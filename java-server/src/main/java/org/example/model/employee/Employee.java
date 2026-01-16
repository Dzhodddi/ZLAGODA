package org.example.model.employee;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Table(name = "employee")
public class Employee implements UserDetails {
    @Id
    @Column(name = "id_employee",
            nullable = false,
            unique = true,
            length = 10)
    private String idEmployee;

    @Column(name = "empl_surname", nullable = false, length = 50)
    private String emplSurname;

    @Column(name = "empl_name", nullable = false, length = 50)
    private String emplName;

    @Column(name = "empl_patronymic", length = 50)
    private String emplPatronymic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "salary", nullable = false, precision = 13, scale = 4)
    private BigDecimal salary;

    @Column(name = "date_of_birth", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "date_of_start", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfStart;

    @Column(name = "phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String street;

    @Column(name = "zip_code", nullable = false, length = 9)
    private String zipCode;

    @Column(nullable = false)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getUsername() {
        return idEmployee;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }
}