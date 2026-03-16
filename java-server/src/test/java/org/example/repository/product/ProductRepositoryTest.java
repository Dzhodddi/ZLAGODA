package org.example.repository.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidCategoryException;
import org.example.mapper.product.ProductMapper;
import org.example.mapper.product.ProductRowMapper;
import org.example.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ProductRowMapper rowMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductRepository repository;

    private Product product;
    private ProductDto productDto;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId_product(1);
        product.setProduct_name("TestProduct");
        product.setProducer("TestProducer");
        product.setCategory_number(10);

        productDto = new ProductDto();
        productDto.setProduct_name("TestProduct");
        productDto.setProducer("TestProducer");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("TestProduct");
        productRequestDto.setProducer("TestProducer");
        productRequestDto.setCategory_number(10);
    }

    @Test
    @DisplayName("findAll should return list of products (No pagination params)")
    void findAll_noParams_shouldReturnProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestProduct", result.getContent().get(0).getProduct_name());
        assertEquals("TestProducer", result.getContent().get(0).getProducer());
    }

    @Test
    @DisplayName("findById should return product when product exists")
    void findById_existingId_shouldReturnProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        Optional<Product> result = repository.findById(1);
        assertTrue(result.isPresent());
        assertEquals("TestProduct", result.get().getProduct_name());
        assertEquals("TestProducer", result.get().getProducer());
    }

    @Test
    @DisplayName("save should insert new product when id is 0")
    void save_newProduct_shouldReturnSavedProduct() {
        Product newProduct = new Product();
        newProduct.setId_product(0);
        newProduct.setProduct_name("TestProduct");
        newProduct.setProducer("TestProducer");
        newProduct.setCategory_number(10);

        Product savedProduct = new Product();
        savedProduct.setId_product(1);
        savedProduct.setProduct_name("TestProduct");
        savedProduct.setProducer("TestProducer");
        savedProduct.setCategory_number(10);

        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                eq("TestProduct"), eq("TestProducer"), eq(null), eq(10)))
                .thenReturn(savedProduct);

        Product result = repository.save(newProduct);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        assertEquals("TestProducer", result.getProducer());
    }

    @Test
    @DisplayName("updateProductById should update and return ProductDto")
    void updateProductById_existingProduct_shouldReturnDto() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyString(), anyString(), any(), anyInt(), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = repository.updateProductById(1, productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        assertEquals("TestProducer", result.getProducer());
    }

    @Test
    @DisplayName("findAllNoPagination should return list of all products")
    void findAllNoPagination_shouldReturnAllProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper))).thenReturn(List.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        List<ProductDto> result = repository.findAllNoPagination();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getProduct_name());
        assertEquals("TestProducer", result.get(0).getProducer());
    }
}
