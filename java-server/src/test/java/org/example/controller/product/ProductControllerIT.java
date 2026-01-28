package org.example.controller.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.product.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

        ProductDto[] products = restClient.get()
                .uri("/products/search?categoryId=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(ProductDto[].class);

        assertNotNull(products);
        assertEquals(2, products.length);
        assertEquals(1L, products[0].getCategory_number());
    }
}
