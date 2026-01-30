package org.example.service.product;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    List<ProductDto> getAllNoPagination();

    PageResponseDto<ProductDto> getAll(Pageable pageable,
                                       String lastSeenName,
                                       int lastSeenId);

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(int id, ProductRequestDto requestDto);

    PageResponseDto<ProductDto> findByName(String name,
                                           Pageable pageable,
                                           String lastSeenName,
                                           int lastSeenId);

    PageResponseDto<ProductDto> findByCategoryId(int category_number,
                                                 Pageable pageable,
                                                 String lastSeenName,
                                                 int lastSeenId);

    void deleteProductById(int id);
}
