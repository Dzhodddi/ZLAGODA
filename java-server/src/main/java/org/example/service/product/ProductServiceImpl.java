package org.example.service.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    @Qualifier("productMapper")
    private final ProductMapper mapper;

    @Override
    public List<ProductDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ProductDto save(ProductRequestDto requestDto) {
        Product product = mapper.toEntity(requestDto);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto updateProductById(Long id, ProductRequestDto requestDto) {
        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found: " + id));

        mapper.updateProductFromDto(requestDto, product);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public void deleteProductById(Long id) {
        repository.deleteById(id);
    }
}
