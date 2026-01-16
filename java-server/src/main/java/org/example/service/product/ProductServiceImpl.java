package org.example.service.product;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getAll() {
        List<Product> entities = productRepository.findAll();
        List<ProductDto> res = new ArrayList<>();
        for (Product entity : entities) {
            res.add(productMapper.toDto(entity));
        }
        return res;
    }

    @Override
    public ProductDto save(ProductRequestDto requestDto) {
        Product product = productMapper.toEntity(requestDto);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto updateProductById(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot update product by id: " + id));
        productMapper.updateProductFromDto(requestDto, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot delete product by id: " + id));
        productRepository.delete(product);
    }
}
