package org.example.controller.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.employee.login.RefreshTokenRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:database/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class RegistrationFlowIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api/v1")
                .build();
    }

    @Test
    @DisplayName("Full registration, login and refresh token flow")
    void registrationAndLoginFlow() throws ParseException {
        EmployeeRegistrationRequestDto registrationRequest = getEmployeeRegistrationRequestDto();

        ResponseEntity<EmployeeResponseDto> registrationResponse =
                restClient.post()
                        .uri("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(registrationRequest)
                        .retrieve()
                        .toEntity(EmployeeResponseDto.class);
        assertEquals(HttpStatus.CREATED, registrationResponse.getStatusCode());

        EmployeeResponseDto registeredEmployee = registrationResponse.getBody();
        assertNotNull(registeredEmployee);
        assertEquals("EMP0003", registeredEmployee.getId_employee());

        EmployeeLoginRequestDto loginRequest = new EmployeeLoginRequestDto();
        loginRequest.setId_employee("EMP0003");
        loginRequest.setPassword("password123");
        ResponseEntity<EmployeeLoginResponseDto> loginResponse =
                restClient.post()
                        .uri("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loginRequest)
                        .retrieve()
                        .toEntity(EmployeeLoginResponseDto.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        EmployeeLoginResponseDto loginBody = loginResponse.getBody();
        assertNotNull(loginBody);
        assertNotNull(loginBody.accessToken());
        assertNotNull(loginBody.refreshToken());

        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setRefreshToken(loginBody.refreshToken());
        ResponseEntity<EmployeeLoginResponseDto> refreshResponse =
                restClient.post()
                        .uri("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(refreshRequest)
                        .retrieve()
                        .toEntity(EmployeeLoginResponseDto.class);
        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());

        EmployeeLoginResponseDto refreshed = refreshResponse.getBody();
        assertNotNull(refreshed);
        assertNotNull(refreshed.accessToken());
        assertNotNull(refreshed.refreshToken());
    }

    private static @NotNull EmployeeRegistrationRequestDto getEmployeeRegistrationRequestDto()
            throws ParseException {
        EmployeeRegistrationRequestDto registrationRequest =
                new EmployeeRegistrationRequestDto();
        registrationRequest.setId_employee("EMP0003");
        registrationRequest.setEmpl_surname("Test");
        registrationRequest.setEmpl_name("User");
        registrationRequest.setRole("MANAGER");
        registrationRequest.setPassword("password123");
        registrationRequest.setRepeat_password("password123");
        registrationRequest.setPhone_number("+380555555555");
        registrationRequest.setCity("Kyiv");
        registrationRequest.setStreet("Khreshchatyk");
        registrationRequest.setZip_code("01001");
        registrationRequest.setSalary(new BigDecimal("15000.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        registrationRequest.setDate_of_birth(sdf.parse("1990-01-15"));
        registrationRequest.setDate_of_start(sdf.parse("2020-05-10"));
        return registrationRequest;
    }
}
