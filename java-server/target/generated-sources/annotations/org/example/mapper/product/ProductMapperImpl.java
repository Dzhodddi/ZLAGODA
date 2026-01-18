package org.example.mapper.product;

import javax.annotation.processing.Generated;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T01:25:29+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        if ( product.getIdProduct() != null ) {
            productDto.setIdProduct( product.getIdProduct().intValue() );
        }
        productDto.setProductName( product.getProductName() );
        productDto.setProductCharacteristics( product.getProductCharacteristics() );
        productDto.setCategoryNumber( product.getCategoryNumber() );

        return productDto;
    }

    @Override
    public Product toEntity(ProductRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Product product = new Product();

        product.setProductName( requestDto.getProductName() );
        product.setProductCharacteristics( requestDto.getProductCharacteristics() );
        product.setCategoryNumber( requestDto.getCategoryNumber() );

        return product;
    }

    @Override
    public void updateProductFromDto(ProductRequestDto requestDto, Product product) {
        if ( requestDto == null ) {
            return;
        }

        product.setProductName( requestDto.getProductName() );
        product.setProductCharacteristics( requestDto.getProductCharacteristics() );
        product.setCategoryNumber( requestDto.getCategoryNumber() );
    }
}
