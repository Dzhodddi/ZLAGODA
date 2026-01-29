package org.example.service.product;

import java.util.List;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    List<ProductDto> getAllNoPagination();

    Page<ProductDto> getAll(Pageable pageable);

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(int id, ProductRequestDto requestDto);

    Page<ProductDto> findByName(String name, Pageable pageable);

    Page<ProductDto> findByCategoryId(int category_number, Pageable pageable);

    void deleteProductById(int id);
}
