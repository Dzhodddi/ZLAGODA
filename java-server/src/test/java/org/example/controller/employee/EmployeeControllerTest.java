package org.example.controller.employee;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.dto.employee.EmployeeContactDto;
import org.example.dto.employee.EmployeeUpdateRequestDto;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private PdfReportGeneratorService pdfReportGeneratorService;

    private EmployeeResponseDto employeeDto1;
    private EmployeeResponseDto employeeDto2;
    private EmployeeRegistrationRequestDto employeeRegistrationRequestDto;
    private EmployeeUpdateRequestDto employeeUpdateRequestDto;
    private EmployeeContactDto employeeContactDto;

    @BeforeEach
    void setup() throws ParseException {
        employeeDto1 = new EmployeeResponseDto();
        employeeDto1.setId_employee("EMP001");
        employeeDto1.setEmpl_surname("Smith");
        employeeDto1.setEmpl_name("John");
        employeeDto1.setEmpl_patronymic("Michael");
        employeeDto1.setRole("CASHIER");
        employeeDto1.setSalary(BigDecimal.valueOf(3000.0));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        employeeDto1.setDate_of_birth(sdf.parse("1990-01-15"));
        employeeDto1.setDate_of_start(sdf.parse("2020-05-10"));
        employeeDto1.setPhone_number("+380501234567");
        employeeDto1.setCity("Kyiv");
        employeeDto1.setStreet("Khreshchatyk");
        employeeDto1.setZip_code("01001");

        employeeDto2 = new EmployeeResponseDto();
        employeeDto2.setId_employee("EMP002");
        employeeDto2.setEmpl_surname("Johnson");
        employeeDto2.setEmpl_name("Jane");
        employeeDto2.setEmpl_patronymic("Ann");
        employeeDto2.setRole("MANAGER");
        employeeDto2.setSalary(BigDecimal.valueOf(5000.0));
        employeeDto2.setDate_of_birth(sdf.parse("1985-03-20"));
        employeeDto2.setDate_of_start(sdf.parse("2018-01-15"));
        employeeDto2.setPhone_number("+380509876543");
        employeeDto2.setCity("Lviv");
        employeeDto2.setStreet("Svobody");
        employeeDto2.setZip_code("79000");

        employeeRegistrationRequestDto = new EmployeeRegistrationRequestDto();
        employeeRegistrationRequestDto.setId_employee("EMP003");
        employeeRegistrationRequestDto.setEmpl_surname("Brown");
        employeeRegistrationRequestDto.setEmpl_name("Robert");
        employeeRegistrationRequestDto.setEmpl_patronymic("James");
        employeeRegistrationRequestDto.setRole("CASHIER");
        employeeRegistrationRequestDto.setSalary(BigDecimal.valueOf(3200.0));
        employeeRegistrationRequestDto.setDate_of_birth(sdf.parse("1992-07-10"));
        employeeRegistrationRequestDto.setDate_of_start(sdf.parse("2021-03-01"));
        employeeRegistrationRequestDto.setPhone_number("+380505555555");
        employeeRegistrationRequestDto.setCity("Odesa");
        employeeRegistrationRequestDto.setStreet("Derybasivska");
        employeeRegistrationRequestDto.setZip_code("65000");
        employeeRegistrationRequestDto.setPassword("SecurePassword123!");

        employeeUpdateRequestDto = new EmployeeUpdateRequestDto();
        employeeUpdateRequestDto.setId_employee("EMP004");
        employeeUpdateRequestDto.setEmpl_surname("Smith");
        employeeUpdateRequestDto.setEmpl_name("John");
        employeeUpdateRequestDto.setEmpl_patronymic("Michael");
        employeeUpdateRequestDto.setRole("MANAGER");
        employeeUpdateRequestDto.setSalary(BigDecimal.valueOf(4000.0));
        employeeUpdateRequestDto.setDate_of_birth(sdf.parse("1990-01-15"));
        employeeUpdateRequestDto.setDate_of_start(sdf.parse("2020-05-10"));
        employeeUpdateRequestDto.setPhone_number("+380501234567");
        employeeUpdateRequestDto.setCity("Kyiv");
        employeeUpdateRequestDto.setStreet("Khreshchatyk");
        employeeUpdateRequestDto.setZip_code("01001");

        employeeContactDto = new EmployeeContactDto();
        employeeContactDto.setPhone_number("+380501234567");
        employeeContactDto.setCity("Kyiv");
        employeeContactDto.setStreet("Khreshchatyk");
        employeeContactDto.setZip_code("01001");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees - Manager should get all employees")
    void getAll_asManager_Ok() throws Exception {
        Page<EmployeeResponseDto> page = new PageImpl<>(new ArrayList<>(
                List.of(employeeDto1, employeeDto2)),
                PageRequest.of(0, 10),
                2);
        when(employeeService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].empl_surname").value("Smith"))
                .andExpect(jsonPath("$.content[1].empl_surname").value("Johnson"))
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(employeeService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /employees - Cashier should get forbidden")
    void getAll_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).getAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees - should return empty list when no employees")
    void getAll_noEmployees_Ok() throws Exception {
        Page<EmployeeResponseDto> emptyPage = new PageImpl<>(
                new ArrayList<>(),
                PageRequest.of(0, 10),
                0
        );

        when(employeeService.getAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(employeeService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /employees - Manager should create employee successfully")
    void createEmployee_asManager_Created() throws Exception {
        EmployeeResponseDto createdEmployee = new EmployeeResponseDto();
        createdEmployee.setId_employee("EMP003");
        createdEmployee.setEmpl_surname("Brown");
        createdEmployee.setRole("CASHIER");
        createdEmployee.setEmpl_name("John");
        createdEmployee.setEmpl_patronymic("Michael");
        createdEmployee.setSalary(BigDecimal.valueOf(4000.0));

        when(employeeService.register(any(EmployeeRegistrationRequestDto.class)))
                .thenReturn(createdEmployee);

        employeeRegistrationRequestDto.setPassword("SecurePassword123!");
        employeeRegistrationRequestDto.setRepeat_password("SecurePassword123!");

        mockMvc.perform(post("/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRegistrationRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_employee").value("EMP003"))
                .andExpect(jsonPath("$.empl_surname").value("Brown"));


        verify(employeeService, times(1))
                .register(any(EmployeeRegistrationRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /employees - should return unprocessable entity for invalid data")
    void createEmployee_invalidData_UnprocessableEntity() throws Exception {
        EmployeeRegistrationRequestDto invalidRequest = new EmployeeRegistrationRequestDto();

        mockMvc.perform(post("/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());

        verify(employeeService, never()).register(any(EmployeeRegistrationRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("PUT /employees/{id} - Manager should update employee successfully")
    void updateEmployee_asManager_Ok() throws Exception {
        EmployeeResponseDto updatedEmployee = new EmployeeResponseDto();
        updatedEmployee.setId_employee("EMP004");
        updatedEmployee.setEmpl_surname("Smith");
        updatedEmployee.setRole("MANAGER");
        updatedEmployee.setSalary(BigDecimal.valueOf(4000.0));

        when(employeeService.updateEmployeeById(eq("EMP004"), any(EmployeeUpdateRequestDto.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/EMP004")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_employee").value("EMP004"))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.salary").value(4000.0));

        verify(employeeService, times(1))
                .updateEmployeeById(eq("EMP004"),
                any(EmployeeUpdateRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("DELETE /employees/{id} - Manager should delete employee successfully")
    void deleteEmployee_asManager_NoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployeeById("EMP001");

        mockMvc.perform(delete("/employees/EMP001")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployeeById("EMP001");
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("DELETE /employees/{id} - Cashier should get forbidden")
    void deleteEmployee_asCashier_Forbidden() throws Exception {
        mockMvc.perform(delete("/employees/EMP001")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).deleteEmployeeById(anyString());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees/report - Manager should download PDF report")
    void employeePdf_asManager_Ok() throws Exception {
        byte[] pdfBytes = "PDF content".getBytes();
        when(employeeService.findAllNoPagination())
                .thenReturn(List.of(employeeDto1, employeeDto2));
        when(pdfReportGeneratorService.employeeToPdf(anyList())).thenReturn(pdfBytes);

        mockMvc.perform(get("/employees/report"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=employees.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().bytes(pdfBytes));

        verify(employeeService, times(1)).findAllNoPagination();
        verify(pdfReportGeneratorService, times(1)).employeeToPdf(anyList());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /employees/report - Cashier should get forbidden")
    void employeePdf_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/employees/report"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).findAllNoPagination();
        verify(pdfReportGeneratorService, never()).employeeToPdf(anyList());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees/cashiers - Manager should get all cashiers")
    void getAllCashiers_asManager_Ok() throws Exception {
        Page<EmployeeResponseDto> page = new PageImpl<>(List.of(employeeDto1),
                PageRequest.of(0, 10),
                1);
        when(employeeService.getAllCashiers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/employees/cashiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].role").value("CASHIER"))
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(employeeService, times(1)).getAllCashiers(any(Pageable.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /employees/cashiers - Cashier should get forbidden")
    void getAllCashiers_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/employees/cashiers"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).getAllCashiers(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees/cashiers - should return empty list when no cashiers")
    void getAllCashiers_noCashiers_Ok() throws Exception {
        Page<EmployeeResponseDto> emptyPage = new PageImpl<>(
                new ArrayList<>(),
                PageRequest.of(0, 10),
                0
        );

        when(employeeService.getAllCashiers(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/employees/cashiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(employeeService, times(1)).getAllCashiers(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees/me - Manager should get forbidden")
    void getMe_asManager_Forbidden() throws Exception {
        mockMvc.perform(get("/employees/me"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).getMe();
    }

    @Test
    @WithMockUser(roles = {"CASHIER", "MANAGER"})
    @DisplayName("GET /employees/me - User with both roles should get forbidden")
    void getMe_withBothRoles_Forbidden() throws Exception {
        mockMvc.perform(get("/employees/me"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).getMe();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees?surname - Manager should find employee contact by surname")
    void findPhoneAndAddressBySurname_asManager_Ok() throws Exception {
        when(employeeService.findPhoneAndAddressBySurname("Smith")).thenReturn(Optional.of(employeeContactDto));

        mockMvc.perform(get("/employees")
                        .param("surname", "Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone_number").value("+380501234567"))
                .andExpect(jsonPath("$.city").value("Kyiv"))
                .andExpect(jsonPath("$.street").value("Khreshchatyk"))
                .andExpect(jsonPath("$.zip_code").value("01001"));

        verify(employeeService, times(1)).findPhoneAndAddressBySurname("Smith");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /employees?surname - should return null when employee not found")
    void findPhoneAndAddressBySurname_notFound_Null() throws Exception {
        when(employeeService.findPhoneAndAddressBySurname("NonExistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees")
                        .param("surname", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(employeeService, times(1)).findPhoneAndAddressBySurname("NonExistent");
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /employees?surname - Cashier should get forbidden")
    void findPhoneAndAddressBySurname_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/employees")
                        .param("surname", "Smith"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).findPhoneAndAddressBySurname(anyString());
    }
}
