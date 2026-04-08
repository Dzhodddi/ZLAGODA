package org.example.repository.store_product;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.example.dto.store_product.product.*;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.mapper.store_product.StoreProductRowMapper;
import org.example.model.store_product.StoreProduct;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Store Product Repository Tests")
class StoreProductRepositoryTest {

    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private StoreProductRowMapper rowMapper;
    @Mock private StoreProductMapper mapper;

    @InjectMocks
    private StoreProductRepository repository;

    private StoreProduct storeProduct;
    private StoreProductRequestDto requestDto;
    private StoreProductDto storeProductDto;

    @BeforeEach
    void setUp() {
        storeProduct = new StoreProduct();
        storeProduct.setUPC("123456789012");
        storeProduct.setId_product(1);

        requestDto = new StoreProductRequestDto();
        requestDto.setUPC("123456789012");
        requestDto.setId_product(1);
        requestDto.setSelling_price(new BigDecimal("10.00"));
        requestDto.setPromotional_product(false);

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
    }

    @Test
    @DisplayName("save should insert non-promotional product")
    void save_shouldInsertProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), anyString()))
                .thenReturn(storeProduct);

        StoreProduct result = repository.save(requestDto);

        assertNotNull(result);
        verify(jdbcTemplate).update(contains("INSERT INTO store_product"),
                eq("123456789012"), any(), eq(1), eq(new BigDecimal("12.00")), anyInt(), eq(false));
    }

    @Test
    @DisplayName("findByUPC should return empty when product not found")
    void findByUPC_notFound_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
                .thenThrow(EmptyResultDataAccessException.class);

        Optional<StoreProductWithNameDto> result = repository.findByUPC("invalid");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("existsByUPC should return true when count > 0")
    void existsByUPC_shouldReturnTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);

        assertTrue(repository.existsByUPC("123456789012"));
    }

    @Test
    @DisplayName("updateByUPC should throw exception if product missing")
    void updateByUPC_notExists_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> repository.updateByUPC("999", requestDto));
    }
}
