package org.example.controller.store_product;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.RestPage;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.store_product.product.StoreProductDto;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "FRONT_URL=http://localhost:3000")
@ActiveProfiles("test")
@Sql(scripts = "classpath:database/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/add-store-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
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

        ResponseEntity<RestPage<StoreProductDto>> response = restClient.get()
                .uri("/store-products?sortedBy=quantity")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<RestPage<StoreProductDto>>() {});

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() > 0);

        List<StoreProductDto> storeProducts = response.getBody().getContent();
        for (int i = 0; i < storeProducts.size() - 1; i++) {
            assertTrue(storeProducts.get(i + 1).getProducts_number()
                            >= storeProducts.get(i).getProducts_number(),
                    "Products should be sorted by quantity in ascending order");
        }
    }
}
