package org.example.service.store_product;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.springframework.data.domain.Pageable;

public interface StoreProductService {

    PageResponseDto<StoreProductDto> getAll(Pageable pageable,
                                            String lastSeenUPC);

    List<StoreProductDto> getAllNoPagination();

    PageResponseDto<StoreProductDto> getAllSortedByQuantity(
            Pageable pageable, String lastSeenUPC);

    PageResponseDto<StoreProductWithNameDto> getAllSortedByName(
            Pageable pageable, String lastSeenUPC);

    PageResponseDto<StoreProductDto> getPromotionalSortedByQuantity(
            Pageable pageable, String lastSeenUPC);

    PageResponseDto<StoreProductDto> getNonPromotionalSortedByQuantity(
            Pageable pageable, String lastSeenUPC);

    PageResponseDto<StoreProductWithNameDto> getPromotionalSortedByName(
            Pageable pageable, String lastSeenUPC);

    PageResponseDto<StoreProductWithNameDto> getNonPromotionalSortedByName(
            Pageable pageable, String lastSeenUPC);

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void softDeleteByUPC(String upc);

    StoreProductCharacteristicsDto findByUPC(String upc);

    StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc);

    PageResponseDto<?> getAll(String sortedBy, Boolean prom, Pageable pageable, String lastSeenUPC);
}
