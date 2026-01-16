package org.example.mapper.product;

import org.example.config.MapperConfig;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ProductMapper {

    @Mapping(source = "id_product", target = "product_id")
    @Mapping(source = "productName", target = "product_name")
    @Mapping(source = "productCharacteristics", target = "product_characteristics")
    @Mapping(source = "categoryNumber", target = "category_number")
    ProductDto toDto(Product product);

    Product toEntity(ProductRequestDto requestDto);

    void updateProductFromDto(ProductRequestDto requestDto,
                              @MappingTarget Product product);
}

