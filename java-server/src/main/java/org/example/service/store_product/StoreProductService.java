package org.example.service.store_product;

import java.util.List;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreProductService {

    Page<StoreProductDto> getAll(Pageable pageable);

    List<StoreProductDto> getAllNoPagination();

    Page<StoreProductDto> getAllSortedByQuantity(Pageable pageable);

    Page<StoreProductWithNameDto> getAllSortedByName(Pageable pageable);

    Page<StoreProductDto> getPromotionalSortedByQuantity(Pageable pageable);

    Page<StoreProductDto> getNonPromotionalSortedByQuantity(Pageable pageable);

    Page<StoreProductWithNameDto> getPromotionalSortedByName(Pageable pageable);

    Page<StoreProductWithNameDto> getNonPromotionalSortedByName(Pageable pageable);

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void softDeleteByUPC(String upc);

    StoreProductCharacteristicsDto findByUPC(String upc);

    StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc);

    Page<?> getAll(String sortedBy, Boolean prom, Pageable pageable);
}
