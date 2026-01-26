package org.example.controller.store_product;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.dto.store_product.product.*;
import org.example.service.report.PdfReportGeneratorService;
import org.example.service.store_product.BatchService;
import org.example.service.store_product.StoreProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Store product management", description = "Endpoints for managing store products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/store-products")
public class StoreProductController {

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
    public List<?> getStoreProducts(
            @RequestParam String sortedBy,
            @RequestParam(required = false) Boolean prom,
            Authentication auth
    ) {
        boolean isCashier = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Cashier"));
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Manager"));
        if ("name".equals(sortedBy) && !isCashier) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only Cashier can sort by name");
        }
        if ("quantity".equals(sortedBy) && !isManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only Manager can sort by quantity");
        }
        return storeProductService.getAll(sortedBy, prom);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new store product",
            description = "Create a new store product"
    )
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
    public void deleteStoreProduct(@PathVariable String upc) {
        storeProductService.softDeleteByUPC(upc);
    }

    @GetMapping("/{upc}")
    @Operation(
            summary = "Find store product info by UPC",
            description = "Returns different data based on query parameters and user role"
    )
    public ResponseEntity<?> findByUpc(
            @PathVariable String upc,
            @RequestParam(required = false) Boolean selling_price,
            @RequestParam(required = false) Boolean quantity,
            @RequestParam(required = false) Boolean name,
            @RequestParam(required = false) Boolean characteristics,
            Authentication auth
    ) {
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Manager"));
        boolean isCashier = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Cashier"));
        if (Boolean.TRUE.equals(selling_price) &&
                Boolean.TRUE.equals(quantity) &&
                Boolean.TRUE.equals(name) &&
                Boolean.TRUE.equals(characteristics)) {
            if (!isManager) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only Manager can access full product characteristics");
            }
            return ResponseEntity.ok(storeProductService.findByUPC(upc));
        }
        if (Boolean.TRUE.equals(selling_price) &&
                Boolean.TRUE.equals(quantity) &&
                name == null &&
                characteristics == null) {
            if (!isCashier) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only Cashier can access price and quantity");
            }
            return ResponseEntity.ok(storeProductService.findPriceAndQuantityByUPC(upc));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid parameter combination. Use either: " +
                        "(selling_price, quantity) for Cashier or " +
                        "(selling_price, quantity, name, characteristics) for Manager");
    }

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Receive new batch of product",
            description = "Add new units of a product."
                    + "If the product already exists, all units are"
                    + "re-priced to the new selling price."
    )
    @PreAuthorize("hasRole('Manager')")
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
    @PreAuthorize("hasRole('Manager')")
    public void deleteExpired() {
        batchService.removeExpired();
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download store products report",
            description = "Download store products pdf report"
    )
    @PreAuthorize("hasRole('Manager')")
    public ResponseEntity<byte[]> storeProductPdf() throws DocumentException {
        List<StoreProductDto> storeProduct = storeProductService.getAllSortedByQuantity();
        byte[] pdf = pdfReportGeneratorService.storeProductToPdf(storeProduct);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=store_products.pdf")
                .body(pdf);
    }
}
