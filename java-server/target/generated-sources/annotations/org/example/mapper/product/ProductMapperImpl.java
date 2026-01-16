package org.example.mapper.product;

import javax.annotation.processing.Generated;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-17T00:12:19+0200",
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

        if ( product.getId_product() != null ) {
            productDto.setProduct_id( product.getId_product().intValue() );
        }
        if ( product.getProductName() != null ) {
            productDto.setProduct_name( product.getProductName() );
        }
        if ( product.getProductCharacteristics() != null ) {
            productDto.setProduct_characteristics( product.getProductCharacteristics() );
        }
        productDto.setCategory_number( product.getCategoryNumber() );

        return productDto;
    }

    @Override
    public Product toEntity(ProductRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Product product = new Product();

        if ( requestDto.getProductName() != null ) {
            product.setProductName( requestDto.getProductName() );
        }
        if ( requestDto.getProductCharacteristics() != null ) {
            product.setProductCharacteristics( requestDto.getProductCharacteristics() );
        }
        product.setCategoryNumber( requestDto.getCategoryNumber() );

        return product;
    }

    @Override
    public void updateProductFromDto(ProductRequestDto requestDto, Product product) {
        if ( requestDto == null ) {
            return;
        }

        if ( requestDto.getProductName() != null ) {
            product.setProductName( requestDto.getProductName() );
        }
        else {
            product.setProductName( null );
        }
        if ( requestDto.getProductCharacteristics() != null ) {
            product.setProductCharacteristics( requestDto.getProductCharacteristics() );
        }
        else {
            product.setProductCharacteristics( null );
        }
        product.setCategoryNumber( requestDto.getCategoryNumber() );
    }
}
