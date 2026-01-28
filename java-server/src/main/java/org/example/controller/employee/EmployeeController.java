package org.example.controller.employee;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.AuthorizationException;
import org.example.exception.RegistrationException;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Employee management", description = "Endpoints for managing employees")
@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping
    @Operation(
            summary = "Get all employees",
            description = "Get all employees sorted by their surnames"
    )
    @PreAuthorize("hasRole('MANAGER')")
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
            @RequestBody @Valid EmployeeUpdateRequestDto employeeRequestDto
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

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download employees report",
            description = "Download employees pdf report"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<byte[]> employeePdf() throws DocumentException, IOException {
        List<EmployeeResponseDto> employees = employeeService.getAll();
        byte[] pdf = pdfReportGeneratorService.employeeToPdf(employees);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employees.pdf")
                .body(pdf);
    }

    @GetMapping("/cashiers")
    @Operation(
            summary = "Get all cashiers",
            description = "Get all cashiers sorted by their surnames"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public List<EmployeeResponseDto> getAllCashiers() {
        return employeeService.getAllCashiers();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get information about authorized cashier",
            description = "Get all information about currently authorized cashier"
    )
    @PreAuthorize("hasRole('CASHIER')")
    public EmployeeResponseDto getMe(Authentication auth) {
        boolean isCashier = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CASHIER"));
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MANAGER"));
        if (!isCashier || isManager) {
            throw new AuthorizationException("Only Cashier can access this endpoint");
        }
        return employeeService.getMe();
    }

    @GetMapping(params = "surname")
    @Operation(
            summary = "Find employee's phone and address by their surname",
            description = "Find employee's phone and address by their surname"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public EmployeeContactDto findPhoneAndAddressBySurname(
            @RequestParam String surname) {
        return employeeService.findPhoneAndAddressBySurname(surname).orElse(null);
    }
}
