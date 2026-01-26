package org.example.service.store_product;

import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.dto.store_product.product.StoreProductDto;

public interface BatchService {
    StoreProductDto save(BatchRequestDto requestDto);

    void removeExpired();
}
