package org.example.service.store_product;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.springframework.data.domain.Pageable;

public interface StoreProductService {

    PageResponseDto<StoreProductWithNameDto> getAll(Pageable pageable);

    List<StoreProductDto> getAllNoPagination();

    List<StoreProductWithNameDto> getAllWithNameNoPagination();

    PageResponseDto<StoreProductWithNameDto> getAllSortedByQuantity(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getAllSortedByName(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getPromotionalSortedByQuantity(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getNonPromotionalSortedByQuantity(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getPromotionalSortedByName(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getNonPromotionalSortedByName(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getPromotional(Pageable pageable);

    PageResponseDto<StoreProductWithNameDto> getNonPromotional(Pageable pageable);

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void softDeleteByUPC(String upc);

    StoreProductWithNameDto findByUPC(String upc);

    StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc);

    PageResponseDto<?> getAll(String sortedBy, Boolean prom, Pageable pageable);
}
