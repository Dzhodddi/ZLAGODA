package org.example.service.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

@ExtendWith(MockitoExtension.class)
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
        productDto.setId_product(2);
        productDto.setProduct_name("TestProduct");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("TestProduct");
    }

    @Test
    @DisplayName("getAll should return list of ProductDto")
    void getAll_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(product));
        when(mapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getProduct_name());
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("save should save product and return ProductDto")
    void save_shouldReturnDto() {
        when(mapper.toEntity(productRequestDto)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(productDto);

        ProductDto result = service.save(productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        verify(repository, times(1)).save(product);
        verify(mapper, times(1)).toEntity(productRequestDto);
        verify(mapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("updateProductById should return updated ProductDto")
    void updateProductById_shouldReturnDto() {
        when(repository.updateProductById(1, productRequestDto)).thenReturn(productDto);

        ProductDto result = service.updateProductById(1, productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        verify(repository, times(1)).updateProductById(1, productRequestDto);
    }

    @Test
    @DisplayName("findByName should return list of ProductDto")
    void findByName_shouldReturnList() {
        when(repository.findByName("TestProduct")).thenReturn(List.of(product));
        when(mapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = service.findByName("TestProduct");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getProduct_name());
        verify(repository, times(1)).findByName("TestProduct");
        verify(mapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("findByCategoryId should return list of ProductDto")
    void findByCategoryId_shouldReturnList() {
        when(repository.findByCategoryId(10)).thenReturn(List.of(product));
        when(mapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = service.findByCategoryId(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getProduct_name());
        verify(repository, times(1)).findByCategoryId(10);
        verify(mapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("deleteProductById should call repository deleteById")
    void deleteProductById_shouldCallRepository() {
        doNothing().when(repository).deleteById(1);

        service.deleteProductById(1);

        verify(repository, times(1)).deleteById(1);
    }
}
