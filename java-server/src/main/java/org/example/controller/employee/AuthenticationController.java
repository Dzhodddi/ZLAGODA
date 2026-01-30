package org.example.controller.employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.employee.login.RefreshTokenRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.exception.custom_exception.RegistrationException;
import org.example.security.AuthenticationService;
import org.example.service.employee.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication and authorization",
        description = "Employee registration and login endpoints"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final EmployeeService employeeService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee",
            description = "Create a new employee")
    public EmployeeResponseDto register(@RequestBody @Valid EmployeeRegistrationRequestDto request)
            throws RegistrationException {
        return employeeService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login employee",
            description = "Authenticate an existing employee")
    public EmployeeLoginResponseDto login(@RequestBody @Valid EmployeeLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/refresh")
    public EmployeeLoginResponseDto refresh(@RequestBody @Valid RefreshTokenRequestDto request) {
        return authenticationService.refreshToken(request);
    }
}
