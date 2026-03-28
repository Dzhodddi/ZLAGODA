package org.example.service.category;

import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CategoryResponseDto;
import org.example.repository.category.CategoryRepository;
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

    @Override
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll();
    }
}
