package org.example.service.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
        return repository.updateProductById(id, requestDto);
    }

    @Override
    public Optional<ProductDto> findByName(String name) {
        Optional<Product> product = repository.findByName(name);
        return product.map(mapper::toDto);
    }

    @Override
    public Optional<ProductDto> findByCategoryId(int category_number) {
        Optional<Product> product = repository.findByCategoryId(category_number);
        return product.map(mapper::toDto);
    }

    @Override
    public void deleteProductById(Long id) {
        repository.deleteById(id);
    }
}
