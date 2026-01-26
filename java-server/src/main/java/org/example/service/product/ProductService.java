package org.example.service.product;

import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAll();

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(Long id, ProductRequestDto requestDto);

    List<ProductDto> findByName(String name);

    List<ProductDto> findByCategoryId(int category_number);

    void deleteProductById(Long id);
}
