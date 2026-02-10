package org.example.controller;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.CategoryResponseDto;
import org.example.service.category.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management",
        description = "Endpoints for custom category endpoint")
@RequiredArgsConstructor
@RestController
@RequestMapping("/{categories}")
public class CategoryController {

    private final static int PAGE_SIZE = 10;
    private final CategoryService categoryService;

    @GetMapping("/top")
    @Operation(
            summary = "Get two most popular categories",
            description = "Get two most popular categories"
    )
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<CategoryResponseDto> getPopCategories() {
        return categoryService.getPopCategories();
    }
}
