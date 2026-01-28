package org.example.mapper.store_product;

import javax.annotation.processing.Generated;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.model.store_product.StoreProduct;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-28T16:58:43+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class StoreProductMapperImpl implements StoreProductMapper {

    @Override
    public StoreProductDto toDto(StoreProduct product) {
        if ( product == null ) {
            return null;
        }

        StoreProductDto storeProductDto = new StoreProductDto();

        storeProductDto.setUPC( product.getUPC() );
        storeProductDto.setUPC_prom( product.getUPC_prom() );
        storeProductDto.setId_product( product.getId_product() );
        storeProductDto.setSelling_price( product.getSelling_price() );
        storeProductDto.setProducts_number( product.getProducts_number() );
        storeProductDto.setPromotional_product( product.isPromotional_product() );

        return storeProductDto;
    }

    @Override
    public StoreProduct toEntity(StoreProductRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        StoreProduct storeProduct = new StoreProduct();

        storeProduct.setUPC( requestDto.getUPC() );
        storeProduct.setUPC_prom( requestDto.getUPC_prom() );
        storeProduct.setId_product( requestDto.getId_product() );
        storeProduct.setProducts_number( requestDto.getProducts_number() );
        storeProduct.setPromotional_product( requestDto.isPromotional_product() );

        return storeProduct;
    }
}
