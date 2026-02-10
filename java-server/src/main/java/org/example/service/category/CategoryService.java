package org.example.service.category;

import java.util.List;
import org.example.dto.CategoryResponseDto;

public interface CategoryService {
    List<CategoryResponseDto> getPopCategories();
}
