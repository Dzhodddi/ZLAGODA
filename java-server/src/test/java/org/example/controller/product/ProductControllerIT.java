package org.example.controller.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.example.RestPage;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.product.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:database/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class ProductControllerIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    public void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api/v1")
                .build();
    }

    @Test
    @DisplayName("GET /products/search?categoryId=1 returns products from this category")
    void searchProductsByCategory_ReturnsExpectedProducts() {
        EmployeeLoginRequestDto loginRequest = new EmployeeLoginRequestDto();
        loginRequest.setId_employee("EMP0001");
        loginRequest.setPassword("password");

        EmployeeLoginResponseDto loginResponse = restClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .retrieve()
                .body(EmployeeLoginResponseDto.class);

        assertNotNull(loginResponse);

        String token = loginResponse.accessToken();

        ResponseEntity<RestPage<ProductDto>> response = restClient.get()
                .uri("/products/search?categoryId=1&lastSeenId=0&lastSeenName=")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<RestPage<ProductDto>>() {});

        assertEquals(0, response.getBody().getNumber());
        assertEquals(2, response.getBody().getTotalElements());
    }
}
