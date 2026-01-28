package org.example.controller.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Date;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.employee.login.RefreshTokenRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.security.AuthenticationService;
import org.example.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void register_shouldReturnCreatedEmployee() throws Exception {
        EmployeeRegistrationRequestDto requestDto = new EmployeeRegistrationRequestDto();
        requestDto.setId_employee("EMP001");
        requestDto.setEmpl_name("John");
        requestDto.setEmpl_surname("Doe");
        requestDto.setEmpl_patronymic("Michael");
        requestDto.setPassword("Password123!");
        requestDto.setRepeat_password("Password123!");
        requestDto.setRole("CASHIER");
        requestDto.setSalary(BigDecimal.valueOf(3000));

        requestDto.setDate_of_birth(new Date(90, 0, 14));
        requestDto.setDate_of_start(new Date(120, 4, 9));
        requestDto.setPhone_number("+380501234567");
        requestDto.setCity("Kyiv");
        requestDto.setStreet("Khreshchatyk");
        requestDto.setZip_code("01001");

        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setId_employee(requestDto.getId_employee());
        responseDto.setEmpl_name(requestDto.getEmpl_name());
        responseDto.setEmpl_surname(requestDto.getEmpl_surname());
        responseDto.setEmpl_patronymic(requestDto.getEmpl_patronymic());
        responseDto.setRole(requestDto.getRole());
        responseDto.setSalary(requestDto.getSalary());
        responseDto.setDate_of_birth(requestDto.getDate_of_birth());
        responseDto.setDate_of_start(requestDto.getDate_of_start());
        responseDto.setPhone_number(requestDto.getPhone_number());
        responseDto.setCity(requestDto.getCity());
        responseDto.setStreet(requestDto.getStreet());
        responseDto.setZip_code(requestDto.getZip_code());

        when(employeeService.register(any(EmployeeRegistrationRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_employee").value("EMP001"))
                .andExpect(jsonPath("$.empl_name").value("John"))
                .andExpect(jsonPath("$.empl_surname").value("Doe"))
                .andExpect(jsonPath("$.role").value("CASHIER"));
    }

    @Test
    void login_shouldReturnEmployeeLoginResponse() throws Exception {
        EmployeeLoginRequestDto loginRequest = new EmployeeLoginRequestDto();
        loginRequest.setId_employee("EMP001");
        loginRequest.setPassword("Password123!");

        EmployeeLoginResponseDto loginResponse
                = new EmployeeLoginResponseDto("access-token",
                "refresh-token");

        when(authenticationService.authenticate(any(EmployeeLoginRequestDto.class)))
                .thenReturn(loginResponse);

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"));
    }

    @Test
    void refresh_shouldReturnNewTokens() throws Exception {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setRefreshToken("refresh-token");

        EmployeeLoginResponseDto refreshResponse
                = new EmployeeLoginResponseDto("new-access-token",
                "new-refresh-token");

        when(authenticationService.refreshToken(any(RefreshTokenRequestDto.class)))
                .thenReturn(refreshResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("new-access-token"))
                .andExpect(jsonPath("$.refresh_token").value("new-refresh-token"));
    }
}
