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
        product.setProduct_characteristics("TestChars");
        product.setCategory_number(10);

        productDto = new ProductDto();
        productDto.setProduct_name("TestProduct");
        productDto.setProducer("TestProducer");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("TestProduct");
        productRequestDto.setProducer("TestProducer");
        productRequestDto.setProduct_characteristics("TestChars");
        productRequestDto.setCategory_number(10);
    }

    @Test
    @DisplayName("findAll should return list of products")
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
    }

    @Test
    @DisplayName("findById should return product when product exists")
    void findById_existingId_shouldReturnProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto); // було відсутнє

        Optional<ProductDto> result = repository.findById(1);

        assertTrue(result.isPresent());
        assertEquals("TestProduct", result.get().getProduct_name());
        assertEquals("TestProducer", result.get().getProducer());
    }

    @Test
    @DisplayName("findById should return empty when product not found")
    void findById_notFound_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(99)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        Optional<ProductDto> result = repository.findById(99);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save should insert new product when id is 0")
    void save_newProduct_shouldReturnSavedProduct() {
        Product newProduct = new Product();
        newProduct.setId_product(0);
        newProduct.setProduct_name("TestProduct");
        newProduct.setProducer("TestProducer");
        newProduct.setProduct_characteristics("TestChars");
        newProduct.setCategory_number(10);

        Product savedProduct = new Product();
        savedProduct.setId_product(1);
        savedProduct.setProduct_name("TestProduct");
        savedProduct.setProducer("TestProducer");
        savedProduct.setProduct_characteristics("TestChars");
        savedProduct.setCategory_number(10);

        // eq(null) не працює — використовуємо isNull() для characteristics
        when(jdbcTemplate.queryForObject(
                anyString(), eq(rowMapper),
                eq("TestProduct"), eq("TestProducer"), eq("TestChars"), eq(10)))
                .thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(productDto); // було відсутнє

        ProductDto result = repository.save(newProduct);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        assertEquals("TestProducer", result.getProducer());
    }

    @Test
    @DisplayName("save should throw InvalidCategoryException on constraint violation")
    void save_invalidCategory_shouldThrow() {
        Product newProduct = new Product();
        newProduct.setId_product(0);
        newProduct.setProduct_name("TestProduct");
        newProduct.setProducer("TestProducer");
        newProduct.setProduct_characteristics("TestChars");
        newProduct.setCategory_number(999);

        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new DataIntegrityViolationException("FK violation"));

        assertThrows(
                org.example.exception.custom_exception.InvalidCategoryException.class,
                () -> repository.save(newProduct)
        );
    }

    @Test
    @DisplayName("updateProductById should update and return ProductDto")
    void updateProductById_existingProduct_shouldReturnDto() {
        // existsByIdProduct
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        // update
        when(jdbcTemplate.update(anyString(),
                eq("TestProduct"), eq("TestProducer"), eq("TestChars"), eq(10), eq(1)))
                .thenReturn(1);
        // findById всередині updateProductById
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = repository.updateProductById(1, productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        assertEquals("TestProducer", result.getProducer());
    }

    @Test
    @DisplayName("updateProductById should throw EntityNotFoundException when not found")
    void updateProductById_notFound_shouldThrow() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> repository.updateProductById(99, productRequestDto)
        );
    }

    @Test
    @DisplayName("deleteById should throw EntityNotFoundException when not found")
    void deleteById_notFound_shouldThrow() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> repository.deleteById(99));
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
    }
}
