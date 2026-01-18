package org.example.controller.employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;
import org.example.service.employee.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employee management", description = "Endpoints for managing employees")
@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(
            summary = "Get all employees",
            description = "Get all employees"
    )
    public List<EmployeeResponseDto> getAll() {
        return employeeService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new employee",
            description = "Create a new employee"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public EmployeeResponseDto createEmployee(
            @RequestBody @Valid EmployeeRegistrationRequestDto employeeRequestDto
    ) throws RegistrationException {
        return employeeService.register(employeeRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an employee",
            description = "Update an existing employee by its id"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public EmployeeResponseDto updateEmployeeById(
            @PathVariable String id,
            @RequestBody @Valid EmployeeRegistrationRequestDto employeeRequestDto
    ) {
        return employeeService.updateEmployeeById(id, employeeRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete an employee",
            description = "Delete an existing employee by its id"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteEmployeeById(@PathVariable String id) {
        employeeService.deleteEmployeeById(id);
    }
}
