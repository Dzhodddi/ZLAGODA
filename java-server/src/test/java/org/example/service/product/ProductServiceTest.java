package org.example.service.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.mapper.product.ProductMapper;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductDto productDto;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId_product(1);
        product.setProduct_name("TestProduct");

        productDto = new ProductDto();
        productDto.setId_product(1);
        productDto.setProduct_name("TestProduct");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("TestProduct");
    }

    @Test
    @DisplayName("getAll should return page of ProductDto")
    void getAll_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> page = PageResponseDto.of(List.of(productDto), 0, 10, 1);

        when(repository.findAll(pageable, "", 0)).thenReturn(page);

        PageResponseDto<ProductDto> result = service.getAll(pageable, "", 0);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestProduct", result.getContent().get(0).getProduct_name());

        verify(repository).findAll(pageable, "", 0);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("save should map, save and return ProductDto")
    void save_ok() {
        when(mapper.toEntity(productRequestDto)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(productDto);

        ProductDto result = service.save(productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());

        verify(mapper).toEntity(productRequestDto);
        verify(repository).save(product);
        verify(mapper).toDto(product);
    }

    @Test
    @DisplayName("updateProductById should return updated ProductDto")
    void update_ok() {
        when(repository.updateProductById(1, productRequestDto))
                .thenReturn(productDto);

        ProductDto result = service.updateProductById(1, productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());

        verify(repository).updateProductById(1, productRequestDto);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findByName should return page of ProductDto")
    void findByName_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> page = PageResponseDto.of(List.of(productDto), 0, 10, 1);

        when(repository.findByName("TestProduct", pageable, "", 0)).thenReturn(page);

        PageResponseDto<ProductDto> result = service.findByName("TestProduct", pageable, "", 0);

        assertEquals(1, result.getContent().size());
        verify(repository).findByName("TestProduct", pageable, "", 0);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("findByCategoryId should return page of ProductDto")
    void findByCategory_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> page = PageResponseDto.of(List.of(productDto), 0, 10, 1);

        when(repository.findByCategoryId(10, pageable, "", 0)).thenReturn(page);

        PageResponseDto<ProductDto> result = service.findByCategoryId(10, pageable, "", 0);

        assertEquals(1, result.getContent().size());
        verify(repository).findByCategoryId(10, pageable, "", 0);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("deleteProductById should call repository")
    void delete_ok() {
        service.deleteProductById(1);

        verify(repository).deleteById(1);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("getAllNoPagination should return list of ProductDto")
    void getAllNoPagination_ok() {
        List<ProductDto> products = List.of(productDto);

        when(repository.findAllNoPagination()).thenReturn(products);

        List<ProductDto> result = service.getAllNoPagination();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getProduct_name());

        verify(repository).findAllNoPagination();
        verifyNoInteractions(mapper);
    }
}
