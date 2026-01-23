package org.example.service.store_product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.StoreProductDto;
import org.example.dto.store_product.StoreProductRequestDto;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.repository.store_product.StoreProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreProductServiceImpl implements StoreProductService {

    private final StoreProductRepository repository;
    @Qualifier("storeProductMapper")
    private final StoreProductMapper mapper;

    @Override
    public List<StoreProductDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public StoreProductDto save(StoreProductRequestDto requestDto) {
        return mapper.toDto(repository.save(requestDto));
    }

    @Override
    public StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto) {
        return repository.updateByUPC(upc, requestDto);
    }

    @Override
    public void softDeleteByUPC(String upc) {
        repository.softDeleteByUPC(upc);
    }
}
