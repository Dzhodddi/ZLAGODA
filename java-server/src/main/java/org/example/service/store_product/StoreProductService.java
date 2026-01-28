package org.example.service.store_product;

import java.util.List;

import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;

public interface StoreProductService {

    List<StoreProductDto> getAll();

    List<StoreProductDto> getAllSortedByQuantity();

    List<StoreProductWithNameDto> getAllSortedByName();

    List<StoreProductDto> getPromotionalSortedByQuantity();

    List<StoreProductDto> getNonPromotionalSortedByQuantity();

    List<StoreProductWithNameDto> getPromotionalSortedByName();

    List<StoreProductWithNameDto> getNonPromotionalSortedByName();

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void softDeleteByUPC(String upc);

    StoreProductCharacteristicsDto findByUPC(String upc);

    StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc);

    List<?> getAll(String sortedBy, Boolean prom);
}
