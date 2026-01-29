package org.example.service.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    @Qualifier("productMapper")
    private final ProductMapper mapper;

    @Override
    public List<ProductDto> getAllNoPagination() {
        return repository.findAllNoPagination();
    }

    @Override
    public Page<ProductDto> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public ProductDto save(ProductRequestDto requestDto) {
        Product product = mapper.toEntity(requestDto);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto updateProductById(int id, ProductRequestDto requestDto) {
        return repository.updateProductById(id, requestDto);
    }

    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        return repository.findByName(name, pageable);
    }

    @Override
    public Page<ProductDto> findByCategoryId(int category_number, Pageable pageable) {
        return repository.findByCategoryId(category_number, pageable);
    }

    @Override
    public void deleteProductById(int id) {
        repository.deleteById(id);
    }
}
