package org.example.controller.store_product;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/add-store-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StoreProductControllerIT {

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
    @DisplayName("GET /store-products?sortedBy=quantity returns store products sorted by quantity")
    void getStoreProductsByQuantity_ReturnsExpectedList() {
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

        StoreProductDto[] storeProducts = restClient.get()
                .uri("/store-products?sortedBy=quantity")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(StoreProductDto[].class);

        assertNotNull(storeProducts);
        assertTrue(storeProducts.length > 0);
        for (int i = 1; i < storeProducts.length - 1; i++) {
            assertTrue(storeProducts[i+1].getProducts_number()
                    >= storeProducts[i].getProducts_number());
        }
    }
}
