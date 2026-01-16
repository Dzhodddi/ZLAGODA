package org.example.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product management", description = "Endpoints for managing products")
@RequiredArgsConstructor
@RestController
@RequestMapping("/V3/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Get all products"
    )
    public List<ProductDto> getAll() {
        return productService.getAll();
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
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
    }
}
