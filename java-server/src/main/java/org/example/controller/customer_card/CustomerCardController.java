package org.example.controller.customer_card;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.helper.CustomerCardResponseDto;
import org.example.dto.product.ProductDto;
import org.example.service.customer_card.CustomerCardService;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Customer card management",
        description = "Endpoints for managing customer cards")
@RequiredArgsConstructor
@RestController
@RequestMapping("/customer-cards")
public class CustomerCardController {

    private final CustomerCardService customerCardService;
    private final EmployeeService employeeService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping()
    @Operation(
            summary = "Get all customer cards",
            description = "Get all customer cards"
    )
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public List<CustomerCardResponseDto> getAll() {
        return customerCardService.getAll();
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download customer cards report",
            description = "Download customer cards pdf report"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> cardPdf() throws DocumentException, IOException {
        List<CustomerCardResponseDto> cards = customerCardService.getAll();
        EmployeeResponseDto manager = employeeService.getMe();
        byte[] pdf = pdfReportGeneratorService.cardToPdf(cards,
                manager.getEmpl_surname() + " " + manager.getEmpl_name());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=products.pdf")
                .body(pdf);
    }
}
