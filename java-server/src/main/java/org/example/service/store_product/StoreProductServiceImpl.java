package org.example.service.store_product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.repository.store_product.StoreProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreProductServiceImpl implements StoreProductService {

    private final StoreProductRepository repository;
    private final StoreProductMapper storeProductMapper;

    public PageResponseDto<?> getAll(String sortedBy,
                                     Boolean prom,
                                     Pageable pageable) {
        if ("name".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByName(pageable);
            }
            return prom
                    ? getPromotionalSortedByName(pageable)
                    : getNonPromotionalSortedByName(pageable);
        }
        if ("quantity".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByQuantity(pageable);
            }
            return prom
                    ? getPromotionalSortedByQuantity(pageable)
                    : getNonPromotionalSortedByQuantity(pageable);
        }
        return getAll(pageable);
    }

    @Override
    public PageResponseDto<StoreProductDto> getAll(
            Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<StoreProductDto> getAllNoPagination() {
        return repository.findAllNoPagination();
    }

    @Override
    public PageResponseDto<StoreProductDto> getAllSortedByQuantity(
            Pageable pageable) {
        return repository.findAllSortedByQuantity(pageable);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getAllSortedByName(
            Pageable pageable) {
        return repository.findAllSortedByName(pageable);
    }

    @Override
    public PageResponseDto<StoreProductDto> getPromotionalSortedByQuantity(
            Pageable pageable) {
        return repository.findPromotionalSortedByQuantity(pageable);
    }

    @Override
    public PageResponseDto<StoreProductDto> getNonPromotionalSortedByQuantity(
            Pageable pageable) {
        return repository.findNonPromotionalSortedByQuantity(pageable);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getPromotionalSortedByName(
            Pageable pageable) {
        return repository.findPromotionalSortedByName(pageable);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getNonPromotionalSortedByName(
            Pageable pageable) {
        return repository.findNonPromotionalSortedByName(pageable);
    }

    @Override
    public StoreProductDto save(StoreProductRequestDto requestDto) {
        return storeProductMapper.toDto(repository.save(requestDto));
    }

    @Override
    public StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto) {
        return repository.updateByUPC(upc, requestDto);
    }

    @Override
    public void softDeleteByUPC(String upc) {
        repository.softDeleteByUPC(upc);
    }

    @Override
    public StoreProductCharacteristicsDto findByUPC(String upc) {
        return repository.findByUPC(upc)
                .orElseThrow(() -> new EntityNotFoundException("No product found with UPC: " + upc));
    }

    @Override
    public StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc) {
        return repository.findPriceAndQuantityByUPC(upc)
                .orElseThrow(() -> new EntityNotFoundException("No product found with UPC: " + upc));
    }
}
