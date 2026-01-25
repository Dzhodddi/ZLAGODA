package org.example.controller.employee;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.RegistrationException;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('Manager')")
    public List<EmployeeResponseDto> getAll() {
        return employeeService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new employee",
            description = "Create a new employee"
    )
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
    public void deleteEmployeeById(@PathVariable String id) {
        employeeService.deleteEmployeeById(id);
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download employees report",
            description = "Download employees pdf report"
    )
    @PreAuthorize("hasRole('Manager')")
    public ResponseEntity<byte[]> employeePdf() throws DocumentException {
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
    @PreAuthorize("hasRole('Manager')")
    public List<EmployeeResponseDto> getAllCashiers() {
        return employeeService.getAllCashiers();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get information about authorized cashier",
            description = "Get all information about currently authorized cashier"
    )
    @PreAuthorize("hasRole('Cashier')")
    public EmployeeResponseDto getMe() {
        return employeeService.getMe();
    }

    @GetMapping("/by-surname/{surname}")
    @Operation(
            summary = "Find employee's phone and address by their surname",
            description = "Find employee's phone and address by their surname"
    )
    @PreAuthorize("hasRole('Manager')")
    public EmployeeContactDto findPhoneAndAddressBySurname(
            @PathVariable String surname) {
        return employeeService.findPhoneAndAddressBySurname(surname);
    }
}
