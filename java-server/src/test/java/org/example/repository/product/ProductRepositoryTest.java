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
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(),
                eq("TestProduct"), eq("TestProducer"), eq("TestChars"), eq(10), eq(1)))
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

    // --- findSold ---

    @Test
    @DisplayName("findSold should return sold products page")
    void findSold_withMinTotalSold_shouldReturnPage() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyDouble(), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyDouble()))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findSold(pageable, 0.0);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findSold should return empty page when nothing matches")
    void findSold_noMatches_shouldReturnEmptyPage() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyDouble(), anyLong(), anyInt()))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyDouble()))
                .thenReturn(0);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findSold(pageable, 99999.0);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("findAllSortedByName should return products sorted by name")
    void findAllSortedByName_shouldReturnSortedProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findAllSortedByName(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findByName should return matching products")
    void findByName_matchingName_shouldReturnProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByName("Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findByName should return empty page when no matches")
    void findByName_noMatches_shouldReturnEmptyPage() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenReturn(0);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByName("nonexistent", pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("findByCategoryId should return products in category")
    void findByCategoryId_existingCategory_shouldReturnProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByCategoryId(10, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findByCategoryId should return empty page for unknown category")
    void findByCategoryId_unknownCategory_shouldReturnEmptyPage() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong(), anyInt()))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenReturn(0);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByCategoryId(999, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("findByCategoryIdSortedByName should return products sorted by name")
    void findByCategoryIdSortedByName_shouldReturnSortedProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong(), anyInt()))
                .thenReturn(List.of(product));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyInt()))
                .thenReturn(1);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDto<ProductDto> result = repository.findByCategoryIdSortedByName(10, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("save should update existing product when id is not 0")
    void save_existingProduct_shouldReturnUpdatedDto() {
        when(jdbcTemplate.update(anyString(),
                eq("TestProduct"), eq("TestProducer"), eq("TestChars"), eq(10), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq(1)))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = repository.save(product);

        assertNotNull(result);
        assertEquals("TestProduct", result.getProduct_name());
    }

    @Test
    @DisplayName("save should throw EntityNotFoundException when update affects 0 rows")
    void save_existingProductNotFound_shouldThrow() {
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> repository.save(product)
        );
    }

    @Test
    @DisplayName("save should throw InvalidCategoryException on constraint violation during update")
    void save_existingProductInvalidCategory_shouldThrow() {
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new DataIntegrityViolationException("FK violation"));

        assertThrows(
                org.example.exception.custom_exception.InvalidCategoryException.class,
                () -> repository.save(product)
        );
    }

    @Test
    @DisplayName("updateProductById should throw InvalidCategoryException on FK violation")
    void updateProductById_invalidCategory_shouldThrow() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(),
                anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new DataIntegrityViolationException("FK violation"));

        assertThrows(
                org.example.exception.custom_exception.InvalidCategoryException.class,
                () -> repository.updateProductById(1, productRequestDto)
        );
    }

    @Test
    @DisplayName("deleteById should delete product when it exists")
    void deleteById_existingProduct_shouldDelete() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);

        assertDoesNotThrow(() -> repository.deleteById(1));
        verify(jdbcTemplate).update(anyString(), eq(1));
    }

    @Test
    @DisplayName("existsByIdProduct should return true when product exists")
    void existsByIdProduct_existing_shouldReturnTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);

        assertTrue(repository.existsByIdProduct(1));
    }

    @Test
    @DisplayName("existsByIdProduct should return false when product does not exist")
    void existsByIdProduct_notExisting_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertFalse(repository.existsByIdProduct(99));
    }

    @Test
    @DisplayName("existsByIdProduct should return false when count is null")
    void existsByIdProduct_nullCount_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(null);

        assertFalse(repository.existsByIdProduct(1));
    }
}
