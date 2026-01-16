package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.model.employee.Role;
import org.example.repository.employee.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        if (roleRepository.findRoleByName(Role.RoleName.CASHIER).isEmpty()) {
            Role cashier = new Role();
            cashier.setName(Role.RoleName.CASHIER);
            roleRepository.save(cashier);
            System.out.println("CASHIER role created with ID: " + cashier.getId());
        }

        if (roleRepository.findRoleByName(Role.RoleName.MANAGER).isEmpty()) {
            Role manager = new Role();
            manager.setName(Role.RoleName.MANAGER);
            roleRepository.save(manager);
            System.out.println("MANAGER role created with ID: " + manager.getId());
        }

        System.out.println("Total roles in database: " + roleRepository.count());
    }
}