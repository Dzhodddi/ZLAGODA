package org.example.service.store_product;

import java.util.List;
import java.util.Optional;

import org.example.dto.store_product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.StoreProductDto;
import org.example.dto.store_product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.StoreProductRequestDto;

public interface StoreProductService {

    List<StoreProductDto> getAllSortedByQuantity();

    List<StoreProductDto> getAllSortedByName();

    List<StoreProductDto> getPromotionalSortedByQuantity();

    List<StoreProductDto> getNonPromotionalSortedByQuantity();

    List<StoreProductDto> getPromotionalSortedByName();

    List<StoreProductDto> getNonPromotionalSortedByName();

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void softDeleteByUPC(String upc);

    StoreProductCharacteristicsDto findByUPC(String upc);

    StoreProductPriceAndQuantityDto findPriceAndQuantityByUPC(String upc);
}
