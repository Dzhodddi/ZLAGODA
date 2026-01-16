package org.example.service.product;

import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductRequestDto requestDto);

    List<ProductDto> findAll();

    ProductDto updateProductById(Long id, ProductRequestDto requestDto);

    void deleteProductById(Long id);
}
