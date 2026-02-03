package org.example.service.report;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PDF Report Generator Service Tests")
class PdfReportGeneratorServiceTest {

    @InjectMocks
    private PdfReportGeneratorServiceImpl service;

    private EmployeeResponseDto employeeDto;
    private ProductDto productDto;
    private StoreProductDto storeProductDto;

    @BeforeEach
    void setUp() {
        employeeDto = new EmployeeResponseDto();
        employeeDto.setId_employee("EMP001");
        employeeDto.setEmpl_surname("Іваненко");
        employeeDto.setEmpl_name("Іван");
        employeeDto.setEmpl_patronymic("Іванович");
        employeeDto.setRole("MANAGER");
        employeeDto.setSalary(new BigDecimal("15000.00"));
        employeeDto.setDate_of_birth(Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        employeeDto.setDate_of_start(Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        employeeDto.setPhone_number("+380501234567");
        employeeDto.setCity("Київ");
        employeeDto.setStreet("Хрещатик 1");
        employeeDto.setZip_code("01001");

        productDto = new ProductDto();
        productDto.setId_product(1);
        productDto.setProduct_name("Test Product");
        productDto.setProduct_characteristics("Test characteristics");
        productDto.setCategory_number(10);

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
        storeProductDto.setUPC_prom("987654321098");
        storeProductDto.setId_product(1);
        storeProductDto.setSelling_price(new BigDecimal("12.00"));
        storeProductDto.setProducts_number(50);
        storeProductDto.setPromotional_product(false);
    }

    @Test
    @DisplayName("employeeToPdf should generate PDF with employees")
    void employeeToPdf_withEmployees_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.employeeToPdf(List.of(employeeDto));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should generate PDF with multiple employees")
    void employeeToPdf_withMultipleEmployees_shouldGeneratePdf() throws DocumentException, IOException {
        EmployeeResponseDto employee2 = new EmployeeResponseDto();
        employee2.setId_employee("EMP002");
        employee2.setEmpl_surname("Петренко");
        employee2.setEmpl_name("Петро");
        employee2.setEmpl_patronymic("Петрович");
        employee2.setRole("CASHIER");
        employee2.setSalary(new BigDecimal("12000.00"));
        employee2.setDate_of_birth(employeeDto.getDate_of_birth());
        employee2.setDate_of_start(employeeDto.getDate_of_start());
        employee2.setPhone_number("+380509876543");
        employee2.setCity("Львів");
        employee2.setStreet("Проспект Свободи 1");
        employee2.setZip_code("79000");

        byte[] result = service.employeeToPdf(List.of(employeeDto, employee2));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should generate PDF with empty list")
    void employeeToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.employeeToPdf(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with products")
    void productToPdf_withProducts_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.productToPdf(List.of(productDto));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with multiple products")
    void productToPdf_withMultipleProducts_shouldGeneratePdf() throws DocumentException, IOException {
        ProductDto product2 = new ProductDto();
        product2.setId_product(2);
        product2.setProduct_name("Another Product");
        product2.setProduct_characteristics("Other characteristics");
        product2.setCategory_number(20);

        byte[] result = service.productToPdf(List.of(productDto, product2));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with empty list")
    void productToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.productToPdf(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should generate PDF with store products")
    void storeProductToPdf_withStoreProducts_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.storeProductToPdf(List.of(storeProductDto));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should generate PDF with multiple store products")
    void storeProductToPdf_withMultipleStoreProducts_shouldGeneratePdf() throws DocumentException, IOException {
        StoreProductDto storeProduct2 = new StoreProductDto();
        storeProduct2.setUPC("111111111111");
        storeProduct2.setUPC_prom(null);
        storeProduct2.setId_product(2);
        storeProduct2.setSelling_price(new BigDecimal("15.00"));
        storeProduct2.setProducts_number(30);
        storeProduct2.setPromotional_product(true);

        byte[] result = service.storeProductToPdf(List.of(storeProductDto, storeProduct2));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should generate PDF with empty list")
    void storeProductToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.storeProductToPdf(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should handle employee with null patronymic")
    void employeeToPdf_nullPatronymic_shouldGeneratePdf() throws DocumentException, IOException {
        employeeDto.setEmpl_patronymic(null);

        byte[] result = service.employeeToPdf(List.of(employeeDto));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should handle promotional products")
    void storeProductToPdf_promotionalProduct_shouldGeneratePdf() throws DocumentException, IOException {
        storeProductDto.setPromotional_product(true);
        storeProductDto.setUPC_prom("987654321098");

        byte[] result = service.storeProductToPdf(List.of(storeProductDto));

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
