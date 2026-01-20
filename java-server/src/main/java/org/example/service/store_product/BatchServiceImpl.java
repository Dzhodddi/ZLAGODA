package org.example.service.store_product;

import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.BatchRequestDto;
import org.example.dto.store_product.StoreProductDto;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.repository.store_product.BatchRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BatchServiceImpl implements BatchService {
    private final BatchRepository batchRepository;
    private final StoreProductMapper storeProductMapper;

    @Override
    public StoreProductDto save(BatchRequestDto requestDto) {
        return storeProductMapper.toDto(batchRepository.save(requestDto));
    }

    @Override
    public void removeExpired() {
        batchRepository.deleteExpired();
    }
}
