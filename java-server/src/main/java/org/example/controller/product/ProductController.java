package org.example.controller.product;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.service.product.ProductService;
import org.example.service.report.PdfReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public List<ProductDto> getAll() {
        return productService.getAll();
    }

    @GetMapping("by-name/{name}")
    @Operation(
            summary = "Search products by name",
            description = "Search products by their name"
    )
    @PreAuthorize("hasRole('Cashier')")
    public Optional<ProductDto> getAllByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @GetMapping("by-category/{id}")
    @Operation(
            summary = "Get all products by their category",
            description = "Get all products by their category"
    )
    @PreAuthorize("hasRole('Manager')")
    public Optional<ProductDto> getAllByName(@PathVariable int id) {
        return productService.findByCategoryId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new product",
            description = "Create a new product"
    )
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
    public ProductDto updateProductById(
            @PathVariable Long id,
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
    @PreAuthorize("hasRole('Manager')")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download products report",
            description = "Download products pdf report"
    )
    @PreAuthorize("hasRole('Manager')")
    public ResponseEntity<byte[]> productPdf() throws DocumentException {
        List<ProductDto> products = productService.getAll();
        byte[] pdf = pdfReportGeneratorService.productToPdf(products);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.pdf")
                .body(pdf);
    }
}
