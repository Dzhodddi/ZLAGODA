package org.example.service.product;

import java.util.List;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;

public interface ProductService {

    List<ProductDto> getAll();

    ProductDto save(ProductRequestDto requestDto);

    ProductDto updateProductById(int id, ProductRequestDto requestDto);

    List<ProductDto> findByName(String name);

    List<ProductDto> findByCategoryId(int category_number);

    void deleteProductById(int id);
}
