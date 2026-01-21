package org.example.controller.store_product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.BatchRequestDto;
import org.example.dto.store_product.StoreProductDto;
import org.example.dto.store_product.StoreProductRequestDto;
import org.example.service.store_product.BatchService;
import org.example.service.store_product.StoreProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Store product management", description = "Endpoints for managing store products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/store-products")
public class StoreProductController {

    private final StoreProductService storeProductService;
    private final BatchService batchService;

    @GetMapping
    @Operation(
            summary = "Get all store products",
            description = "Retrieve all store products"
    )
    public List<StoreProductDto> getAll() {
        return storeProductService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new store product",
            description = "Create a new store product"
    )
    @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteStoreProduct(@PathVariable String upc) {
        storeProductService.deleteByUPC(upc);
    }

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Receive new batch of product",
            description = "Add new units of a product."
                    + "If the product already exists, all units are"
                    + "re-priced to the new selling price."
    )
    @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteExpired() {
        batchService.removeExpired();
    }
}
