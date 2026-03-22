package org.example.service.product;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductDto getById(int id);

    PageResponseDto<ProductDto> getSold(Pageable pageable);

    List<ProductDto> getAllNoPagination();

    PageResponseDto<ProductDto> getAll(Pageable pageable);

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(int id, ProductRequestDto requestDto);

    PageResponseDto<ProductDto> findByName(String name,
                                           Pageable pageable);

    PageResponseDto<ProductDto> findByCategoryId(int category_number,
                                                 Pageable pageable);

    void deleteProductById(int id);
}
