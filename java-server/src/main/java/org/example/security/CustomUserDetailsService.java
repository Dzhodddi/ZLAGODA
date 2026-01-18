package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.repository.employee.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return employeeRepository.findByIdEmployee(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Employee with id " + id + " not found"));
    }
}
