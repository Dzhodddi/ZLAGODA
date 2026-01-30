package org.example.controller.product;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.custom_exception.AuthorizationException;
import org.example.exception.custom_exception.InvalidParameterException;
import org.example.service.product.ProductService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product management", description = "Endpoints for managing products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Get all products sorted by their names"
    )
    public PageResponseDto<ProductDto> getAll(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String lastSeenName,
                                              @RequestParam(required = false) Integer lastSeenId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("product_name"));
        return productService.getAll(pageable, lastSeenName, lastSeenId);
    }

    @GetMapping(value = "/search")
    @Operation(
            summary = "Search products",
            description = "Search products by their name or category"
    )
    public PageResponseDto<ProductDto> search(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) Integer categoryId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String lastSeenName,
                                              @RequestParam(required = false) Integer lastSeenId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCashier = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CASHIER"));
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("product_name"));
        if (name != null && !name.isEmpty()) {
            if (!isCashier) {
                throw new AuthorizationException("Only Cashier can search products by name");
            }
            return productService.findByName(name, pageable, lastSeenName, lastSeenId);
        }
        if (categoryId != null) {
            return productService.findByCategoryId(categoryId, pageable, lastSeenName, lastSeenId);
        }
        throw new InvalidParameterException("Provide an appropriate parameter");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new product",
            description = "Create a new product"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ProductDto createProduct(
            @RequestBody @Valid ProductRequestDto productRequestDto
    ) {
        return productService.save(productRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a product",
            description = "Update an existing product by its id"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ProductDto updateProductById(
            @PathVariable int id,
            @RequestBody @Valid ProductRequestDto productRequestDto
    ) {
        return productService.updateProductById(id, productRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a product",
            description = "Delete an existing product by its id"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteProductById(@PathVariable int id) {
        productService.deleteProductById(id);
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download products report",
            description = "Download products pdf report"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<byte[]> productPdf() throws DocumentException, IOException {
        List<ProductDto> products = productService.getAllNoPagination();
        byte[] pdf = pdfReportGeneratorService.productToPdf(products);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=products.pdf")
                .body(pdf);
    }
}
