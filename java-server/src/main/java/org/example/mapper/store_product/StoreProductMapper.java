package org.example.mapper.store_product;

import org.example.dto.store_product.product.StoreProductDto;
import org.example.model.store_product.StoreProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreProductMapper {

    StoreProductDto toDto(StoreProduct product);
}
