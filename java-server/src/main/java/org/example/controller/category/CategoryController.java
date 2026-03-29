package org.example.controller.category;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.helper.CategoryResponseDto;
import org.example.dto.product.ProductDto;
import org.example.service.category.CategoryService;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management",
        description = "Endpoints for custom category endpoint")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final EmployeeService employeeService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping("/top")
    @Operation(
            summary = "Get two most popular categories",
            description = "Get two most popular categories"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<CategoryResponseDto> getPopCategories() {
        return categoryService.getPopCategories();
    }

    @GetMapping()
    @Operation(
            summary = "Get two most popular categories",
            description = "Get two most popular categories"
    )
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CASHIER')")
    public List<CategoryResponseDto> getAll() {
        return categoryService.getAll();
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download products report",
            description = "Download products pdf report"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> categoryPdf() throws DocumentException, IOException {
        List<CategoryResponseDto> categories = categoryService.getAll();
        EmployeeResponseDto manager = employeeService.getMe();
        byte[] pdf = pdfReportGeneratorService.categoryToPdf(categories,
                manager.getEmpl_surname() + " " + manager.getEmpl_name());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=products.pdf")
                .body(pdf);
    }
}
