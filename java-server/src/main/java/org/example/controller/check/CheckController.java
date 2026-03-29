package org.example.controller.check;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.dto.helper.CheckResponseDto;
import org.example.dto.product.ProductDto;
import org.example.service.check.CheckService;
import org.example.service.employee.EmployeeService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Check management",
        description = "Endpoints for managing checks")
@RequiredArgsConstructor
@RestController
@RequestMapping("/checks")
public class CheckController {

    private final CheckService checkService;
    private final EmployeeService employeeService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping()
    @Operation(
            summary = "Get all checks",
            description = "Get all checks"
    )
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public List<CheckResponseDto> getAll() {
        return checkService.getAll();
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download products report",
            description = "Download checks pdf report"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> checkPdf() throws DocumentException, IOException {
        List<CheckResponseDto> checks = checkService.getAll();
        EmployeeResponseDto manager = employeeService.getMe();
        byte[] pdf = pdfReportGeneratorService.checkToPdf(checks,
                manager.getEmpl_surname() + " " + manager.getEmpl_name());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=products.pdf")
                .body(pdf);
    }
}
