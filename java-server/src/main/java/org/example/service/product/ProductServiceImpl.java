package org.example.service.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.custom_exception.InvalidProductException;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getById(int id) {
        return repository.findById(id).orElseThrow(()
                -> new InvalidProductException("No product with such id: " + id));
    }

    @Override
    public PageResponseDto<ProductDto> getSold(Pageable pageable) {
        return repository.findSold(pageable);
    }

    @Override
    public List<ProductDto> getAllNoPagination() {
        return repository.findAllNoPagination();
    }

    @Override
    public PageResponseDto<ProductDto> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public ProductDto save(ProductRequestDto requestDto) {
        Product product = productMapper.toEntity(requestDto);
        return repository.save(product);
    }

    @Override
    public ProductDto updateProductById(int id, ProductRequestDto requestDto) {
        return repository.updateProductById(id, requestDto);
    }

    @Override
    public PageResponseDto<ProductDto> findByName(String name,
                                                  Pageable pageable) {
        return repository.findByName(name, pageable);
    }

    @Override
    public PageResponseDto<ProductDto> findByCategoryId(int category_number,
                                                        Pageable pageable) {
        return repository.findByCategoryId(category_number, pageable);
    }

    @Override
    public void deleteProductById(int id) {
        repository.deleteById(id);
    }
}
