package org.example.mapper.product;

import org.example.config.MapperConfig;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductRequestDto requestDto);

    void updateProductFromDto(ProductRequestDto requestDto,
                              @MappingTarget Product product);
}

