package org.example.controller.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.service.product.ProductService;
import org.example.service.report.PdfReportGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private PdfReportGeneratorService pdfReportGeneratorService;

    private ProductDto productDto1;
    private ProductDto productDto2;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setup() {
        productDto1 = new ProductDto();
        productDto1.setId_product(1);
        productDto1.setProduct_name("Apple");
        productDto1.setProduct_characteristics("Tasty apple");
        productDto1.setCategory_number(10);

        productDto2 = new ProductDto();
        productDto2.setId_product(2);
        productDto2.setProduct_name("Banana");
        productDto2.setProduct_characteristics("Delicious banana");
        productDto2.setCategory_number(20);

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("Orange");
        productRequestDto.setProduct_characteristics("Wonderful orange");
        productRequestDto.setCategory_number(10);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products - should return all products")
    void getAll_allAvailableProducts_Ok() throws Exception {
        when(productService.getAll()).thenReturn(List.of(productDto1, productDto2));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Apple"))
                .andExpect(jsonPath("$[1].product_name").value("Banana"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(productService, times(1)).getAll();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products - should return empty list when no products")
    void getAll_noProducts_Ok() throws Exception {
        when(productService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).getAll();
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /products/search?name - Cashier should search by name successfully")
    void search_byName_asCashier_Ok() throws Exception {
        when(productService.findByName("Apple")).thenReturn(List.of(productDto1));

        mockMvc.perform(get("/products/search")
                        .param("name", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Apple"))
                .andExpect(jsonPath("$.length()").value(1));

        verify(productService, times(1)).findByName("Apple");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products/search?name - Non-Cashier should get forbidden")
    void search_byName_asNonCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("name", "Apple"))
                .andExpect(status().isForbidden());

        verify(productService, never()).findByName(anyString());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products/search?categoryId - should search by category")
    void search_byCategoryId_Ok() throws Exception {
        when(productService.findByCategoryId(10)).thenReturn(List.of(productDto1));

        mockMvc.perform(get("/products/search")
                        .param("categoryId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Apple"))
                .andExpect(jsonPath("$.length()").value(1));

        verify(productService, times(1)).findByCategoryId(10);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products/search - should return bad request when no parameters")
    void search_noParameters_BadRequest() throws Exception {
        mockMvc.perform(get("/products/search"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).findByName(anyString());
        verify(productService, never()).findByCategoryId(anyInt());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /products/search?name - should return empty list when no matches")
    void search_byName_noMatches_Ok() throws Exception {
        when(productService.findByName("NonExistent")).thenReturn(List.of());

        mockMvc.perform(get("/products/search")
                        .param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).findByName("NonExistent");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /products - Manager should create product successfully")
    void createProduct_asManager_Created() throws Exception {
        ProductDto createdProduct = new ProductDto();
        createdProduct.setId_product(3);
        createdProduct.setProduct_name("Orange");
        createdProduct.setProduct_characteristics("Juicy orange");
        createdProduct.setCategory_number(10);

        when(productService.save(any(ProductRequestDto.class))).thenReturn(createdProduct);

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_product").value(3))
                .andExpect(jsonPath("$.product_name").value("Orange"));

        verify(productService, times(1)).save(any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("POST /products - Non-Manager should get forbidden")
    void createProduct_asNonManager_Forbidden() throws Exception {
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).save(any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /products - should return unprocessable entity exception for invalid data")
    void createProduct_invalidData_UnprocessableEntity() throws Exception {
        ProductRequestDto invalidRequest = new ProductRequestDto();
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());

        verify(productService, never()).save(any(ProductRequestDto.class));
    }


    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("PUT /products/{id} - Manager should update product successfully")
    void updateProduct_asManager_Ok() throws Exception {
        ProductDto updatedProduct = new ProductDto();
        updatedProduct.setId_product(1);
        updatedProduct.setProduct_name("Updated Apple");
        updatedProduct.setProduct_characteristics("Updated description");
        updatedProduct.setCategory_number(10);

        when(productService.updateProductById(eq(1), any(ProductRequestDto.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_product").value(1))
                .andExpect(jsonPath("$.product_name").value("Updated Apple"));

        verify(productService, times(1)).updateProductById(eq(1), any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("PUT /products/{id} - Non-Manager should get forbidden")
    void updateProduct_asNonManager_Forbidden() throws Exception {
        mockMvc.perform(put("/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).updateProductById(anyInt(), any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("DELETE /products/{id} - Manager should delete product successfully")
    void deleteProduct_asManager_NoContent() throws Exception {
        doNothing().when(productService).deleteProductById(1);

        mockMvc.perform(delete("/products/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProductById(1);
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("DELETE /products/{id} - Non-Manager should get forbidden")
    void deleteProduct_asNonManager_Forbidden() throws Exception {
        mockMvc.perform(delete("/products/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteProductById(anyInt());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("GET /products/report - Manager should download PDF report")
    void productPdf_asManager_Ok() throws Exception {
        byte[] pdfBytes = "PDF content".getBytes();

        when(productService.getAll()).thenReturn(List.of(productDto1, productDto2));
        when(pdfReportGeneratorService.productToPdf(anyList())).thenReturn(pdfBytes);

        mockMvc.perform(get("/products/report"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=products.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().bytes(pdfBytes));

        verify(productService, times(1)).getAll();
        verify(pdfReportGeneratorService, times(1)).productToPdf(anyList());
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("GET /products/report - Non-Manager should get forbidden")
    void productPdf_asNonManager_Forbidden() throws Exception {
        mockMvc.perform(get("/products/report"))
                .andExpect(status().isForbidden());

        verify(productService, never()).getAll();
        verify(pdfReportGeneratorService, never()).productToPdf(anyList());
    }
}
