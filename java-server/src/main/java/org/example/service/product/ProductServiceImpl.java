package org.example.service.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public PageResponseDto<ProductDto> getAll(Pageable pageable,
                                              String lastSeenName,
                                              int lastSeenId) {
        return repository.findAll(pageable, lastSeenName, lastSeenId);
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
    public PageResponseDto<ProductDto> findByName(String name,
                                                  Pageable pageable,
                                                  String lastSeenName,
                                                  int lastSeenId) {
        return repository.findByName(name, pageable, lastSeenName, lastSeenId);
    }

    @Override
    public PageResponseDto<ProductDto> findByCategoryId(int category_number,
                                             Pageable pageable,
                                             String lastSeenName,
                                             int lastSeenId) {
        return repository.findByCategoryId(category_number, pageable, lastSeenName, lastSeenId);
    }

    @Override
    public void deleteProductById(int id) {
        repository.deleteById(id);
    }
}
