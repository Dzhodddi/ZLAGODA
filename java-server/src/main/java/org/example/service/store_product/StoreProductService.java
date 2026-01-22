package org.example.service.store_product;

import java.util.List;
import org.example.dto.store_product.StoreProductDto;
import org.example.dto.store_product.StoreProductRequestDto;

public interface StoreProductService {

    List<StoreProductDto> getAll();

    StoreProductDto save(StoreProductRequestDto requestDto);

    StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto);

    void deleteByUPC(String upc);
}
