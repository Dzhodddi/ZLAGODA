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
                                     Pageable pageable,
                                     String lastSeenUPC) {
        if ("name".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByName(pageable, lastSeenUPC);
            }
            return prom
                    ? getPromotionalSortedByName(pageable, lastSeenUPC)
                    : getNonPromotionalSortedByName(pageable, lastSeenUPC);
        }
        if ("quantity".equals(sortedBy)) {
            if (prom == null) {
                return getAllSortedByQuantity(pageable, lastSeenUPC);
            }
            return prom
                    ? getPromotionalSortedByQuantity(pageable, lastSeenUPC)
                    : getNonPromotionalSortedByQuantity(pageable, lastSeenUPC);
        }
        return getAll(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductDto> getAll(
            Pageable pageable, String lastSeenUPC) {
        return repository.findAll(pageable, lastSeenUPC);
    }

    @Override
    public List<StoreProductDto> getAllNoPagination() {
        return repository.findAllNoPagination();
    }

    @Override
    public PageResponseDto<StoreProductDto> getAllSortedByQuantity(
            Pageable pageable, String lastSeenUPC) {
        return repository.findAllSortedByQuantity(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getAllSortedByName(
            Pageable pageable, String lastSeenUPC) {
        return repository.findAllSortedByName(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductDto> getPromotionalSortedByQuantity(
            Pageable pageable, String lastSeenUPC) {
        return repository.findPromotionalSortedByQuantity(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductDto> getNonPromotionalSortedByQuantity(
            Pageable pageable, String lastSeenUPC) {
        return repository.findNonPromotionalSortedByQuantity(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getPromotionalSortedByName(
            Pageable pageable, String lastSeenUPC) {
        return repository.findPromotionalSortedByName(pageable, lastSeenUPC);
    }

    @Override
    public PageResponseDto<StoreProductWithNameDto> getNonPromotionalSortedByName(
            Pageable pageable, String lastSeenUPC) {
        return repository.findNonPromotionalSortedByName(pageable, lastSeenUPC);
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
