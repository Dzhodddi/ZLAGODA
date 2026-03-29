package org.example.service.report;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.example.dto.helper.CategoryResponseDto;
import org.example.dto.helper.CheckResponseDto;
import org.example.dto.helper.CustomerCardResponseDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("PDF Report Generator Service Tests")
class PdfReportGeneratorServiceTest {

    @InjectMocks
    private PdfReportGeneratorServiceImpl service;

    private EmployeeResponseDto employeeDto;
    private ProductDto productDto;
    private StoreProductDto storeProductDto;
    private CustomerCardResponseDto cardDto;
    private CheckResponseDto checkDto;
    private CategoryResponseDto categoryDto;

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
        productDto.setProduct_name("Test Product");
        productDto.setProduct_characteristics("Test characteristics");

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
        storeProductDto.setUPC_prom("987654321098");
        storeProductDto.setId_product(1);
        storeProductDto.setSelling_price(new BigDecimal("12.00"));
        storeProductDto.setProducts_number(50);
        storeProductDto.setPromotional_product(false);

        cardDto = new CustomerCardResponseDto();
        cardDto.setCard_number("1234567890123");
        cardDto.setCust_surname("Коваленко");
        cardDto.setCust_name("Олена");
        cardDto.setCust_patronymic("Василівна");
        cardDto.setPhone_number("+380671234567");
        cardDto.setCity("Одеса");
        cardDto.setStreet("Дерибасівська 5");
        cardDto.setZip_code("65000");
        cardDto.setPercent(5);

        checkDto = new CheckResponseDto();
        checkDto.setCheck_number("CHK0001");
        checkDto.setId_employee("EMP001");
        checkDto.setCard_number("1234567890123");
        checkDto.setPrint_date(LocalDateTime.of(2024, 1, 15, 10, 30));
        checkDto.setSum_total(new BigDecimal("250.00"));
        checkDto.setVat(new BigDecimal("50.00"));

        categoryDto = new CategoryResponseDto();
        categoryDto.setCategory_number(1);
        categoryDto.setCategory_name("Молочні продукти");
    }

    @Test
    @DisplayName("employeeToPdf should generate PDF with employees")
    void employeeToPdf_withEmployees_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.employeeToPdf(List.of(employeeDto), "Test Manager");

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

        byte[] result = service.employeeToPdf(List.of(employeeDto, employee2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should generate PDF with empty list")
    void employeeToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.employeeToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with products")
    void productToPdf_withProducts_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.productToPdf(List.of(productDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with multiple products")
    void productToPdf_withMultipleProducts_shouldGeneratePdf() throws DocumentException, IOException {
        ProductDto product2 = new ProductDto();
        product2.setProduct_name("Another Product");
        product2.setProduct_characteristics("Other characteristics");

        byte[] result = service.productToPdf(List.of(productDto, product2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("productToPdf should generate PDF with empty list")
    void productToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.productToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should generate PDF with store products")
    void storeProductToPdf_withStoreProducts_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.storeProductToPdf(List.of(storeProductDto), "Test Manager");

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

        byte[] result = service.storeProductToPdf(List.of(storeProductDto, storeProduct2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should generate PDF with empty list")
    void storeProductToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.storeProductToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should handle employee with null patronymic")
    void employeeToPdf_nullPatronymic_shouldGeneratePdf() throws DocumentException, IOException {
        employeeDto.setEmpl_patronymic(null);

        byte[] result = service.employeeToPdf(List.of(employeeDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should handle promotional products")
    void storeProductToPdf_promotionalProduct_shouldGeneratePdf() throws DocumentException, IOException {
        storeProductDto.setPromotional_product(true);
        storeProductDto.setUPC_prom("987654321098");

        byte[] result = service.storeProductToPdf(List.of(storeProductDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("cardToPdf should generate PDF with customer cards")
    void cardToPdf_withCards_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.cardToPdf(List.of(cardDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("cardToPdf should generate PDF with multiple customer cards")
    void cardToPdf_withMultipleCards_shouldGeneratePdf() throws DocumentException, IOException {
        CustomerCardResponseDto card2 = new CustomerCardResponseDto();
        card2.setCard_number("9876543210123");
        card2.setCust_surname("Мельник");
        card2.setCust_name("Тарас");
        card2.setCust_patronymic("Олегович");
        card2.setPhone_number("+380931234567");
        card2.setCity("Харків");
        card2.setStreet("Сумська 10");
        card2.setZip_code("61000");
        card2.setPercent(10);

        byte[] result = service.cardToPdf(List.of(cardDto, card2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("cardToPdf should generate PDF with empty list")
    void cardToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.cardToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("checkToPdf should generate PDF with checks")
    void checkToPdf_withChecks_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.checkToPdf(List.of(checkDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("checkToPdf should generate PDF with multiple checks")
    void checkToPdf_withMultipleChecks_shouldGeneratePdf() throws DocumentException, IOException {
        CheckResponseDto check2 = new CheckResponseDto();
        check2.setCheck_number("CHK0002");
        check2.setId_employee("EMP002");
        check2.setCard_number(null);
        check2.setPrint_date(LocalDateTime.of(2024, 2, 20, 14, 0));
        check2.setSum_total(new BigDecimal("100.00"));
        check2.setVat(new BigDecimal("20.00"));

        byte[] result = service.checkToPdf(List.of(checkDto, check2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("checkToPdf should generate PDF with empty list")
    void checkToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.checkToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("categoryToPdf should generate PDF with categories")
    void categoryToPdf_withCategories_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.categoryToPdf(List.of(categoryDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("categoryToPdf should generate PDF with multiple categories")
    void categoryToPdf_withMultipleCategories_shouldGeneratePdf() throws DocumentException, IOException {
        CategoryResponseDto category2 = new CategoryResponseDto();
        category2.setCategory_number(2);
        category2.setCategory_name("Хлібобулочні вироби");

        byte[] result = service.categoryToPdf(List.of(categoryDto, category2), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("categoryToPdf should generate PDF with empty list")
    void categoryToPdf_emptyList_shouldGeneratePdf() throws DocumentException, IOException {
        byte[] result = service.categoryToPdf(Collections.emptyList(), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("employeeToPdf should handle CASHIER role")
    void employeeToPdf_cashierRole_shouldGeneratePdf() throws DocumentException, IOException {
        employeeDto.setRole("CASHIER");

        byte[] result = service.employeeToPdf(List.of(employeeDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("storeProductToPdf should handle null UPC_prom")
    void storeProductToPdf_nullUpcProm_shouldGeneratePdf() throws DocumentException, IOException {
        storeProductDto.setUPC_prom(null);

        byte[] result = service.storeProductToPdf(List.of(storeProductDto), "Test Manager");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
