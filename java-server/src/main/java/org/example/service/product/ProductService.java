package org.example.service.product;

import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductDto> getAll();

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(Long id, ProductRequestDto requestDto);

    Optional<ProductDto> findByName(String name);

    Optional<ProductDto> findByCategoryId(int category_number);

    void deleteProductById(Long id);
}
