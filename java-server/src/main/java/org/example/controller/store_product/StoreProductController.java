package org.example.controller.store_product;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.service.report.PdfReportGeneratorService;
import org.example.service.store_product.BatchService;
import org.example.service.store_product.StoreProductService;
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

@Tag(name = "Store product management",
        description = "Endpoints for managing store products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/store-products")
public class StoreProductController {

    private final static int PAGE_SIZE = 10;
    private final StoreProductService storeProductService;
    private final BatchService batchService;
    private final PdfReportGeneratorService pdfReportGeneratorService;

    @GetMapping
    @Operation(
            summary = "Get all store products",
            description = """
    Get all store products with optional filters:
    - sortedBy: name | quantity
    - prom: true | false
    """
    )
    public PageResponseDto<?> getStoreProducts(
            @RequestParam(required = false, name = "sorted_by") String sortedBy,
            @RequestParam(required = false) Boolean prom,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        Pageable pageable;
        if ("name".equals(sortedBy)) {
            pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("product_name"));
        } else if ("quantity".equals(sortedBy)) {
            pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("products_number"));
        } else {
            pageable = PageRequest.of(page, PAGE_SIZE);
        }
        return storeProductService.getAll(sortedBy, prom, pageable);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all store products without pagination",
            description = "Get all store products without pagination"
    )
    public List<StoreProductWithNameDto> getAllWithoutPagination() {
        return storeProductService.getAllWithNameNoPagination();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new store product",
            description = "Create a new store product"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public StoreProductDto createStoreProduct(
            @RequestBody @Valid StoreProductRequestDto requestDto
    ) {
        return storeProductService.save(requestDto);
    }

    @PutMapping("/{upc}")
    @Operation(
            summary = "Update a store product",
            description = "Update an existing store product by its UPC"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public StoreProductDto updateStoreProduct(
            @PathVariable String upc,
            @RequestBody @Valid StoreProductRequestDto requestDto
    ) {
        return storeProductService.updateByUPC(upc, requestDto);
    }

    @DeleteMapping("/{upc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a store product",
            description = "Delete an existing store product by its UPC"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public void deleteStoreProduct(@PathVariable String upc) {
        storeProductService.softDeleteByUPC(upc);
    }

    @GetMapping("/{upc}")
    @Operation(
            summary = "Find store product info by UPC",
            description = "Returns different data based on user role"
    )
    public ResponseEntity<?> findByUpc(
            @PathVariable String upc
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MANAGER"));
        boolean isCashier = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CASHIER"));
        if (isManager) {
            return ResponseEntity.ok(storeProductService.findByUPC(upc));
        } else if (isCashier) {
            return ResponseEntity.ok(storeProductService.findPriceAndQuantityByUPC(upc));
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Receive new batch of product",
            description = "Add new units of a product."
                    + "If the product already exists, all units are"
                    + "re-priced to the new selling price."
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public StoreProductDto receiveNewBatch(
            @RequestBody @Valid BatchRequestDto requestDto
    ) {
        return batchService.save(requestDto);
    }

    @DeleteMapping("/expired")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete expired batches",
            description = "Deletes all batches whose expiration date has passed"
                    + "and updates store product quantities"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public void deleteExpired() {
        batchService.removeExpired();
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download store products report",
            description = "Download store products pdf report"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> storeProductPdf() throws DocumentException, IOException {
        List<StoreProductDto> storeProduct = storeProductService.getAllNoPagination();
        byte[] pdf = pdfReportGeneratorService.storeProductToPdf(storeProduct);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=store_products.pdf")
                .body(pdf);
    }
}
