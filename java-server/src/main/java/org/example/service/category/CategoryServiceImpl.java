package org.example.service.category;

import lombok.RequiredArgsConstructor;
import org.example.dto.CategoryResponseDto;
import org.example.repository.CategoryRepository;
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
