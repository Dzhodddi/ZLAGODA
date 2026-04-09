package org.example.service.store_product;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.*;
import org.springframework.data.domain.Pageable;

public interface StoreProductService {

    StoreProductCharacteristicsDto getProductInfoByUPC(String upc);

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

    void deleteByUPC(String upc);

    StoreProductWithNameDto getByUPC(String upc);

    StoreProductPriceAndQuantityDto getPriceAndQuantityByUPC(String upc);

    PageResponseDto<?> getAll(String sortedBy, Boolean prom, Pageable pageable);
}
