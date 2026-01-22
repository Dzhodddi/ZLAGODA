package org.example.service.store_product;

import org.example.dto.store_product.BatchRequestDto;
import org.example.dto.store_product.StoreProductDto;

public interface BatchService {
    StoreProductDto save(BatchRequestDto requestDto);

    void removeExpired();
}
