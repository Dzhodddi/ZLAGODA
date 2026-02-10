package org.example.service.product.category;

import java.util.List;
import org.example.dto.helper.CategoryResponseDto;

public interface CategoryService {
    List<CategoryResponseDto> getPopCategories();
}
