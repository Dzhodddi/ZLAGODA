package org.example.service.store_product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.product.*;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.repository.store_product.StoreProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreProductServiceImpl implements StoreProductService {

    private final StoreProductRepository repository;
    @Qualifier("storeProductMapper")
    private final StoreProductMapper mapper;

    public List<?> getAll(String sortedBy, Boolean prom) {
        if ("name".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByName();
            }
            return prom
                    ? getPromotionalSortedByName()
                    : getNonPromotionalSortedByName();
        }
        if ("quantity".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByQuantity();
            }
            return prom
                    ? getPromotionalSortedByQuantity()
                    : getNonPromotionalSortedByQuantity();
        }
        throw new IllegalArgumentException("Unsupported sortedBy value: " + sortedBy);
    }


    @Override
    public List<StoreProductDto> getAllSortedByQuantity() {
        return repository.findAllSortedByQuantity()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<StoreProductWithNameDto> getAllSortedByName() {
        return repository.findAllSortedByName();
    }

    @Override
    public List<StoreProductDto> getPromotionalSortedByQuantity() {
        return repository.findPromotionalSortedByQuantity()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<StoreProductDto> getNonPromotionalSortedByQuantity() {
        return repository.findNonPromotionalSortedByQuantity()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<StoreProductWithNameDto> getPromotionalSortedByName() {
        return repository.findPromotionalSortedByName();
    }

    @Override
    public List<StoreProductWithNameDto> getNonPromotionalSortedByName() {
        return repository.findNonPromotionalSortedByName();
    }

    @Override
    public StoreProductDto save(StoreProductRequestDto requestDto) {
        return mapper.toDto(repository.save(requestDto));
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
