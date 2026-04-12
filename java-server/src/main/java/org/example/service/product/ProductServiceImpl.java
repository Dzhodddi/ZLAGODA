package org.example.service.product;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidProductException;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getById(int id) {
        return repository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("No product with such id: " + id));
    }

    @Override
    public ProductDto getProductSoldQuantityForPeriod(int id_product,
                                                                LocalDate startDate,
                                                                LocalDate endDate) {
        return repository.findProductSoldQuantityForPeriod(id_product, startDate, endDate)
                .orElseThrow(() -> new InvalidProductException("No product with such id: " + id_product));
    }

    @Override
    public PageResponseDto<ProductDto> getSold(Pageable pageable, Double minTotalSold) {
        return repository.findSold(pageable, minTotalSold);
    }

    @Override
    public List<ProductDto> getAllNoPagination() {
        return repository.findAllNoPagination();
    }

    @Override
    public PageResponseDto<ProductDto> getAll(Pageable pageable, boolean sortedByName) {
        if (sortedByName) {
            return repository.findAllSortedByName(pageable);
        }
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
                                                        Pageable pageable, boolean sortedByName) {
        if (sortedByName) {
            return repository.findByCategoryIdSortedByName(category_number, pageable);
        }
        return repository.findByCategoryId(category_number, pageable);
    }

    @Override
    public void deleteProductById(int id) {
        repository.deleteById(id);
    }
}
