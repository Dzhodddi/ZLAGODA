package org.example.service.product.category;

import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CategoryResponseDto;
import org.example.repository.product.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDto> getPopCategories() {
        return categoryRepository.findPopCategories();
    }
}
