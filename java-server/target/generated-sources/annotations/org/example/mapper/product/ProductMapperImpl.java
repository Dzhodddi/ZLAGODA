package org.example.mapper.product;

import javax.annotation.processing.Generated;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.model.product.Product;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T12:04:04+0300",
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

        productDto.setId_product( product.getId_product() );
        productDto.setProduct_name( product.getProduct_name() );
        productDto.setProducer( product.getProducer() );
        productDto.setProduct_characteristics( product.getProduct_characteristics() );
        productDto.setCategory_number( product.getCategory_number() );
        productDto.setCategory_name( product.getCategory_name() );
        productDto.setSold_number( product.getSold_number() );
        productDto.setTotal_sold( product.getTotal_sold() );

        return productDto;
    }

    @Override
    public Product toEntity(ProductRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Product product = new Product();

        product.setProduct_name( requestDto.getProduct_name() );
        product.setProducer( requestDto.getProducer() );
        product.setProduct_characteristics( requestDto.getProduct_characteristics() );
        product.setCategory_number( requestDto.getCategory_number() );

        return product;
    }
}
