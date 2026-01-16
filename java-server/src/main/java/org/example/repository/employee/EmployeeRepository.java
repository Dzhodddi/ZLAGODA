package org.example.repository.employee;

import org.example.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmplSurname(String emplSurname);

    Optional<Employee> findByEmplSurname(String surname);
}
