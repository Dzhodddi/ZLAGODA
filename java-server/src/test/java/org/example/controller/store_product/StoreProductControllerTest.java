package org.example.controller.store_product;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.service.report.PdfReportGeneratorService;
import org.example.service.store_product.BatchService;
import org.example.service.store_product.StoreProductService;
import org.jetbrains.annotations.NotNull;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "FRONT_URL=http://localhost:3000")
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
class StoreProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreProductService storeProductService;

    @MockBean
    private BatchService batchService;

    @MockBean
    private PdfReportGeneratorService pdfReportGeneratorService;

    private StoreProductDto storeProductDto1;
    private StoreProductDto storeProductDto2;
    private StoreProductRequestDto storeProductRequestDto;
    private BatchRequestDto batchRequestDto;
    private ProductDto productDto;

    @BeforeEach
    void setup() throws ParseException {
        productDto = new ProductDto();
        productDto.setProduct_characteristics("product_characteristics");
        productDto.setProduct_name("product_name");

        storeProductDto1 = new StoreProductDto();
        storeProductDto1.setUPC("1234567890");
        storeProductDto1.setUPC_prom(null);
        storeProductDto1.setId_product(1);
        storeProductDto1.setSelling_price(BigDecimal.valueOf(100.0));
        storeProductDto1.setProducts_number(50);
        storeProductDto1.setPromotional_product(false);

        storeProductDto2 = new StoreProductDto();
        storeProductDto2.setUPC("0987654321");
        storeProductDto2.setUPC_prom(null);
        storeProductDto2.setId_product(2);
        storeProductDto2.setSelling_price(BigDecimal.valueOf(200.0));
        storeProductDto2.setProducts_number(30);
        storeProductDto2.setPromotional_product(false);

        storeProductRequestDto = new StoreProductRequestDto();
        storeProductRequestDto.setUPC("1111111111");
        storeProductRequestDto.setId_product(3);
        storeProductRequestDto.setPrice(BigDecimal.valueOf(150.0));
        storeProductRequestDto.setProducts_number(25);
        storeProductRequestDto.setPromotional_product(false);

        batchRequestDto = new BatchRequestDto();
        batchRequestDto.setUPC("1234567890");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        batchRequestDto.setDelivery_date(sdf.parse("2025-01-02"));
        batchRequestDto.setExpiring_date(sdf.parse("2026-01-02"));
        batchRequestDto.setPrice(BigDecimal.valueOf(105.0));
        batchRequestDto.setQuantity(20);
    }

    private @NotNull StoreProductWithNameDto getProductWithNameDto(
            StoreProductDto storeProductDto2, boolean promotional) {
        StoreProductWithNameDto dtoWithName2 = new StoreProductWithNameDto();
        dtoWithName2.setProduct_name(productDto.getProduct_name());
        dtoWithName2.setId_product(storeProductDto2.getId_product());
        dtoWithName2.setProducts_number(storeProductDto2.getProducts_number());
        dtoWithName2.setSelling_price(storeProductDto2.getSelling_price());
        dtoWithName2.setPromotional_product(promotional);
        dtoWithName2.setUPC(storeProductDto2.getUPC());
        dtoWithName2.setUPC_prom(storeProductDto2.getUPC_prom());
        return dtoWithName2;
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products?sortedBy=name - Manager should get forbidden when sorting by name")
    void getStoreProducts_sortedByName_asManager_Forbidden() throws Exception {
        mockMvc.perform(get("/store-products")
                        .param("sortedBy", "name"))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).getAll(anyString(), any(), any(Pageable.class), any());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /store-products?sortedBy=name - Cashier should get products sorted by name")
    void getStoreProducts_sortedByName_asCashier_Ok() throws Exception {
        PageResponseDto<StoreProductWithNameDto> page = PageResponseDto.of(
                new ArrayList<>(List.of(getProductWithNameDto(storeProductDto1, false))),
                0,
                10,
                false
        );
        when(storeProductService.getAll(eq("name"), any(),
                any(Pageable.class),
                isNull()))
                .thenReturn((PageResponseDto) page);

        mockMvc.perform(get("/store-products")
                        .param("sortedBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].upc").value("1234567890"));

        verify(storeProductService, times(1))
                .getAll(eq("name"), any(), any(Pageable.class), isNull());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products?sortedBy=quantity - Manager should get products sorted by quantity")
    void getStoreProducts_sortedByQuantity_asManager_Ok() throws Exception {
        PageResponseDto<StoreProductDto> page = PageResponseDto.of(
                new ArrayList<>(List.of(storeProductDto1, storeProductDto2)),
                0,
                10,
                false
        );
        when(storeProductService.getAll(eq("quantity"), any(),
                any(Pageable.class),
                isNull()))
                .thenReturn((PageResponseDto) page);

        mockMvc.perform(get("/store-products")
                        .param("sortedBy", "quantity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(storeProductService, times(1))
                .getAll(eq("quantity"), any(), any(Pageable.class), isNull());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /store-products?sortedBy=quantity - Cashier should get forbidden when sorting by quantity")
    void getStoreProducts_sortedByQuantity_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/store-products")
                        .param("sortedBy", "quantity"))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).getAll(anyString(), any(), any(Pageable.class), any());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("POST /store-products - Manager should create store product successfully")
    void createStoreProduct_asManager_Created() throws Exception {
        when(storeProductService.save(any(StoreProductRequestDto.class))).thenReturn(storeProductDto1);

        mockMvc.perform(post("/store-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeProductRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.upc").value("1234567890"))
                .andExpect(jsonPath("$.selling_price").value(100.0));

        verify(storeProductService, times(1)).save(any(StoreProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("POST /store-products - Cashier should get forbidden")
    void createStoreProduct_asCashier_Forbidden() throws Exception {
        mockMvc.perform(post("/store-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeProductRequestDto)))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).save(any(StoreProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("POST /store-products - should return unprocessable entity for invalid data")
    void createStoreProduct_invalidData_UnprocessableEntity() throws Exception {
        StoreProductRequestDto invalidRequest = new StoreProductRequestDto();

        mockMvc.perform(post("/store-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());

        verify(storeProductService, never()).save(any(StoreProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("PUT /store-products/{upc} - Manager should update store product successfully")
    void updateStoreProduct_asManager_Ok() throws Exception {
        StoreProductDto updatedProduct = new StoreProductDto();
        updatedProduct.setUPC("1234567890");
        updatedProduct.setSelling_price(BigDecimal.valueOf(110.0));
        updatedProduct.setProducts_number(60);

        when(storeProductService.updateByUPC(eq("1234567890"), any(StoreProductRequestDto.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/store-products/1234567890")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeProductRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upc").value("1234567890"))
                .andExpect(jsonPath("$.selling_price").value(110.0));

        verify(storeProductService, times(1)).updateByUPC(eq("1234567890"),
                any(StoreProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("PUT /store-products/{upc} - Cashier should get forbidden")
    void updateStoreProduct_asCashier_Forbidden() throws Exception {
        mockMvc.perform(put("/store-products/1234567890")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeProductRequestDto)))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).updateByUPC(anyString(), any(StoreProductRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("DELETE /store-products/{upc} - Manager should delete store product successfully")
    void deleteStoreProduct_asManager_NoContent() throws Exception {
        doNothing().when(storeProductService).softDeleteByUPC("1234567890");

        mockMvc.perform(delete("/store-products/1234567890")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(storeProductService, times(1)).softDeleteByUPC("1234567890");
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("DELETE /store-products/{upc} - Cashier should get forbidden")
    void deleteStoreProduct_asCashier_Forbidden() throws Exception {
        mockMvc.perform(delete("/store-products/1234567890")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).softDeleteByUPC(anyString());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products/{upc}?selling_price=true&quantity=true&name=true&characteristics=true"
            + "- Manager should get full product info")
    void findByUpc_fullInfo_asManager_Ok() throws Exception {
        StoreProductCharacteristicsDto dto = new StoreProductCharacteristicsDto();
        dto.setProducts_number(storeProductDto1.getProducts_number());
        dto.setSelling_price(storeProductDto1.getSelling_price());
        dto.setProduct_name(productDto.getProduct_name());
        dto.setProduct_characteristics(productDto.getProduct_characteristics());

        when(storeProductService.findByUPC("1234567890")).thenReturn(dto);

        mockMvc.perform(get("/store-products/1234567890")
                        .param("selling_price", "true")
                        .param("quantity", "true")
                        .param("name", "true")
                        .param("characteristics", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selling_price").value(100.0))
                .andExpect(jsonPath("$.products_number").value(50))
                .andExpect(jsonPath("$.product_name").value("product_name"))
                .andExpect(jsonPath("$.product_characteristics").value("product_characteristics"));

        verify(storeProductService, times(1)).findByUPC("1234567890");
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /store-products/{upc}?selling_price=true&quantity=true&name=true&characteristics=true - Cashier should get forbidden")
    void findByUpc_fullInfo_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/store-products/1234567890")
                        .param("selling_price", "true")
                        .param("quantity", "true")
                        .param("name", "true")
                        .param("characteristics", "true"))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).findByUPC(anyString());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /store-products/{upc}?selling_price=true&quantity=true"
            + " - Cashier should get price and quantity")
    void findByUpc_priceAndQuantity_asCashier_Ok() throws Exception {
        StoreProductPriceAndQuantityDto dto = new StoreProductPriceAndQuantityDto();
        dto.setSelling_price(BigDecimal.valueOf(100.0));
        dto.setProducts_number(50);
        when(storeProductService.findPriceAndQuantityByUPC("1234567890"))
                .thenReturn(dto);

        mockMvc.perform(get("/store-products/1234567890")
                        .param("selling_price", "true")
                        .param("quantity", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selling_price").value(100.0))
                .andExpect(jsonPath("$.products_number").value(50));

        verify(storeProductService, times(1)).findPriceAndQuantityByUPC("1234567890");
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products/{upc}?selling_price=true&quantity=true"
            + " - Manager should get forbidden for price and quantity only")
    void findByUpc_priceAndQuantity_asManager_Forbidden() throws Exception {
        mockMvc.perform(get("/store-products/1234567890")
                        .param("selling_price", "true")
                        .param("quantity", "true"))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).findPriceAndQuantityByUPC(anyString());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products/{upc} - should return bad request for invalid parameter combination")
    void findByUpc_invalidParameters_BadRequest() throws Exception {
        mockMvc.perform(get("/store-products/1234567890")
                        .param("selling_price", "true"))
                .andExpect(status().isBadRequest());

        verify(storeProductService, never()).findByUPC(anyString());
        verify(storeProductService, never()).findPriceAndQuantityByUPC(anyString());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("POST /store-products/receive - Manager should receive new batch successfully")
    void receiveNewBatch_asManager_Created() throws Exception {
        when(batchService.save(any(BatchRequestDto.class))).thenReturn(storeProductDto1);

        mockMvc.perform(post("/store-products/receive")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.upc").value("1234567890"))
                .andExpect(jsonPath("$.selling_price").value(100.0));

        verify(batchService, times(1)).save(any(BatchRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("POST /store-products/receive - Cashier should get forbidden")
    void receiveNewBatch_asCashier_Forbidden() throws Exception {
        mockMvc.perform(post("/store-products/receive")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequestDto)))
                .andExpect(status().isForbidden());

        verify(batchService, never()).save(any(BatchRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("POST /store-products/receive - should return unprocessable entity for invalid batch data")
    void receiveNewBatch_invalidData_UnprocessableEntity() throws Exception {
        BatchRequestDto invalidRequest = new BatchRequestDto();

        mockMvc.perform(post("/store-products/receive")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());

        verify(batchService, never()).save(any(BatchRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("DELETE /store-products/expired - Manager should delete expired batches successfully")
    void deleteExpired_asManager_NoContent() throws Exception {
        doNothing().when(batchService).removeExpired();

        mockMvc.perform(delete("/store-products/expired")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(batchService, times(1)).removeExpired();
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("DELETE /store-products/expired - Cashier should get forbidden")
    void deleteExpired_asCashier_Forbidden() throws Exception {
        mockMvc.perform(delete("/store-products/expired")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(batchService, never()).removeExpired();
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    @DisplayName("GET /store-products/report - Manager should download PDF report")
    void storeProductPdf_asManager_Ok() throws Exception {
        byte[] pdfBytes = "PDF content".getBytes();
        when(storeProductService.getAllNoPagination()).thenReturn(List.of(storeProductDto1, storeProductDto2));
        when(pdfReportGeneratorService.storeProductToPdf(anyList())).thenReturn(pdfBytes);

        mockMvc.perform(get("/store-products/report"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=store_products.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().bytes(pdfBytes));

        verify(storeProductService, times(1)).getAllNoPagination();
        verify(pdfReportGeneratorService, times(1)).storeProductToPdf(anyList());
    }

    @Test
    @WithMockUser(authorities = "CASHIER")
    @DisplayName("GET /store-products/report - Cashier should get forbidden")
    void storeProductPdf_asCashier_Forbidden() throws Exception {
        mockMvc.perform(get("/store-products/report"))
                .andExpect(status().isForbidden());

        verify(storeProductService, never()).getAllNoPagination();
        verify(pdfReportGeneratorService, never()).storeProductToPdf(anyList());
    }
}
