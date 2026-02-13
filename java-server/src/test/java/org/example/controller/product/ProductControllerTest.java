package org.example.controller.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.example.dto.page.PageResponseDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "FRONT_URL=http://localhost:3000")
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
    void setUp() {
        productDto1 = new ProductDto();
        productDto1.setProduct_name("Apple");
        productDto1.setProduct_characteristics("Tasty apple");

        productDto2 = new ProductDto();
        productDto2.setProduct_name("Banana");
        productDto2.setProduct_characteristics("Delicious banana");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("Orange");
        productRequestDto.setProduct_characteristics("Juicy orange");
        productRequestDto.setCategory_number(10);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /products - should return paged products")
    void getAll_ok() throws Exception {
        PageResponseDto<ProductDto> page = PageResponseDto.of(
                new ArrayList<>(List.of(productDto1, productDto2)),
                0,
                10,
                false
        );
        when(productService.getAll(any(Pageable.class), anyInt())).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("lastSeenName", "")
                        .param("lastSeenId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].product_name").value("Apple"))
                .andExpect(jsonPath("$.content[1].product_name").value("Banana"));

        verify(productService).getAll(any(Pageable.class), anyInt());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /products?name - cashier allowed")
    void searchByName_cashier_ok() throws Exception {
        PageResponseDto<ProductDto> page = PageResponseDto.of(
                new ArrayList<>(List.of(productDto1)),
                0,
                10,
                false
        );
        when(productService.findByName(eq("Apple"), any(Pageable.class), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("name", "Apple")
                        .param("lastSeenId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].product_name").value("Apple"));

        verify(productService).findByName(eq("Apple"), any(Pageable.class), anyInt());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /products?name - manager forbidden")
    void searchByName_manager_forbidden() throws Exception {
        mockMvc.perform(get("/products")
                        .param("name", "Apple")
                        .param("lastSeenId", "0"))
                .andExpect(status().isForbidden());

        verify(productService, never()).findByName(anyString(), any(), anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /products?categoryId - allowed")
    void searchByCategory_ok() throws Exception {
        PageResponseDto<ProductDto> page = PageResponseDto.of(
                new ArrayList<>(List.of(productDto1, productDto2)),
                0,
                10,
                false
        );
        when(productService.findByCategoryId(eq(10), any(Pageable.class), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("categoryId", "10")
                        .param("lastSeenId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(productService).findByCategoryId(eq(10), any(Pageable.class), anyInt());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("POST /products - manager can create")
    void createProduct_ok() throws Exception {
        ProductDto created = new ProductDto();
        created.setProduct_name("Orange");

        when(productService.save(any(ProductRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_name").value("Orange"));

        verify(productService).save(any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("POST /products - cashier forbidden")
    void createProduct_forbidden() throws Exception {
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("PUT /products/{id} - manager can update")
    void updateProduct_ok() throws Exception {
        ProductDto updated = new ProductDto();
        updated.setProduct_name("Updated Apple");

        when(productService.updateProductById(eq(1), any(ProductRequestDto.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value("Updated Apple"));

        verify(productService).updateProductById(eq(1), any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("DELETE /products/{id} - manager can delete")
    void deleteProduct_ok() throws Exception {
        doNothing().when(productService).deleteProductById(1);

        mockMvc.perform(delete("/products/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductById(1);
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /products/report - pdf download")
    void productReport_ok() throws Exception {
        byte[] pdf = "PDF".getBytes();

        when(productService.getAllNoPagination()).thenReturn(List.of(productDto1, productDto2));
        when(pdfReportGeneratorService.productToPdf(anyList())).thenReturn(pdf);

        mockMvc.perform(get("/products/report"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=products.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));

        verify(productService).getAllNoPagination();
        verify(pdfReportGeneratorService).productToPdf(anyList());
    }
}
