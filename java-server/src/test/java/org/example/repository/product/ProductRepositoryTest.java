package org.example.repository.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        product.setCategory_number(10);

        productDto = new ProductDto();
        productDto.setId_product(1);
        productDto.setProduct_name("TestProduct");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProduct_name("TestProduct");
        productRequestDto.setCategory_number(10);
    }

    @Test
    @DisplayName("findAll should return list of products")
    void findAll_shouldReturnProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), any(), any(), any(), any()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class)
        )).thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findAll(pageable, "", 0);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestProduct", result.getContent().get(0).getProduct_name());
    }

    @Test
    @DisplayName("findById should return product when product exists")
    void findById_existingId_shouldReturnProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        Optional<Product> result = repository.findById(1);
        assertTrue(result.isPresent());
        assertEquals("TestProduct", result.get().getProduct_name());
    }

    @Test
    @DisplayName("findById should return empty when product does not exist")
    void findById_nonExistingId_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(2)))
                .thenThrow(EmptyResultDataAccessException.class);
        Optional<Product> result = repository.findById(2);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByName should return products with matching name")
    void findByName_shouldReturnProducts() {
        // Виправлено: передаємо 5 any() для відповідності виклику (name, name, name, id, pageSize)
        when(jdbcTemplate.query(anyString(), eq(rowMapper), any(), any(), any(), any(), any()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq("TestProduct")
        )).thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByName("TestProduct",
                pageable, "", 0);

        assertEquals(1, result.getContent().size());
        assertEquals("TestProduct", result.getContent().get(0).getProduct_name());
    }

    @Test
    @DisplayName("findByCategoryId should return products in category")
    void findByCategoryId_shouldReturnProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), any(), any(), any(), any(), any()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(Integer.class),
                eq(10))
        ).thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByCategoryId(10,
                pageable, "", 0);

        assertEquals(1, result.getContent().size());
        assertEquals("TestProduct", result.getContent().get(0).getProduct_name());
    }

    @Test
    @DisplayName("save should insert new product when id is 0")
    void save_newProduct_shouldReturnSavedProduct() {
        Product newProduct = new Product();
        newProduct.setId_product(0);
        newProduct.setProduct_name("TestProduct");
        newProduct.setCategory_number(10);

        Product savedProduct = new Product();
        savedProduct.setId_product(1);
        savedProduct.setProduct_name("TestProduct");
        savedProduct.setCategory_number(10);

        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                eq("TestProduct"), eq(null), eq(10))).thenReturn(savedProduct);

        Product result = repository.save(newProduct);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
    }

    @Test
    @DisplayName("save should update existing product when id is not 0")
    void save_existingProduct_shouldUpdateAndReturnProduct() {
        when(jdbcTemplate.update(anyString(), anyString(), any(), anyInt(), anyInt()))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);

        Product result = repository.save(product);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        verify(jdbcTemplate, times(1)).update(anyString(),
                anyString(), any(), anyInt(), anyInt());
        verify(jdbcTemplate, times(1)).queryForObject(
                anyString(), eq(rowMapper), eq(1));
    }

    @Test
    @DisplayName("save should throw EntityNotFoundException when update affects 0 rows")
    void save_existingProduct_notFound_shouldThrowException() {
        when(jdbcTemplate.update(anyString(), anyString(), any(), anyInt(), anyInt()))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> repository.save(product));
    }

    @Test
    @DisplayName("save should throw InvalidCategoryException on data integrity violation")
    void save_invalidCategory_shouldThrowException() {
        Product newProduct = new Product();
        newProduct.setId_product(0);
        newProduct.setProduct_name("TestProduct");
        newProduct.setCategory_number(999);

        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                eq("TestProduct"), eq(null), eq(999)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidCategoryException.class, () -> repository.save(newProduct));
    }

    @Test
    @DisplayName("updateProductById should update and return ProductDto when product exists")
    void updateProductById_existingProduct_shouldReturnDto() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyString(), any(), anyInt(), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = repository.updateProductById(1, productRequestDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
        verify(jdbcTemplate, times(1)).update(anyString(),
                anyString(), any(), anyInt(), eq(1));
    }

    @Test
    @DisplayName("updateProductById should throw EntityNotFoundException when product does not exist")
    void updateProductById_nonExistingProduct_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(2)))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateProductById(2, productRequestDto));
    }

    @Test
    @DisplayName("updateProductById should throw InvalidCategoryException on data integrity violation")
    void updateProductById_invalidCategory_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyString(), any(), anyInt(), eq(1)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidCategoryException.class,
                () -> repository.updateProductById(1, productRequestDto));
    }

    @Test
    @DisplayName("deleteById should delete product when it exists")
    void deleteById_existingProduct_shouldDeleteProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);

        repository.deleteById(1);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(1));
    }

    @Test
    @DisplayName("deleteById should throw EntityNotFoundException when product does not exist")
    void deleteById_nonExistingProduct_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(2)))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> repository.deleteById(2));
    }

    @Test
    @DisplayName("existsByIdProduct should return true when product exists")
    void existsByIdProduct_existingProduct_shouldReturnTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);

        assertTrue(repository.existsByIdProduct(1));
    }

    @Test
    @DisplayName("existsByIdProduct should return false when product does not exist")
    void existsByIdProduct_nonExistingProduct_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(2)))
                .thenReturn(0);
        assertFalse(repository.existsByIdProduct(2));
    }
}
