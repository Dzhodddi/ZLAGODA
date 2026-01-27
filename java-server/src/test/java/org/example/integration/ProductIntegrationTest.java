package org.example.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getAll_ReturnsProductsFromDatabase() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].product_name").value("Apple"))
                .andExpect(jsonPath("$[0].product_characteristics").exists())
                .andExpect(jsonPath("$[1].product_name").value("Banana"));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void search_ByName_AsCashier_ReturnsProducts() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("name", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Apple"));
    }

    @Test
    @WithMockUser
    void search_ByName_AsNonCashier_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("name", "Apple"))
                .andExpect(status().isForbidden());
    }
}