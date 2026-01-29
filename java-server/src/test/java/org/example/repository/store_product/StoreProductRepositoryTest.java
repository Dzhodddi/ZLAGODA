package org.example.repository.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidProductException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.mapper.store_product.StoreProductRowMapper;
import org.example.model.store_product.StoreProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Store Product Repository Tests")
class StoreProductRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private StoreProductRowMapper rowMapper;

    @Mock
    private StoreProductMapper mapper;

    @InjectMocks
    private StoreProductRepository repository;

    private StoreProduct storeProduct;
    private StoreProductRequestDto requestDto;
    private StoreProductDto storeProductDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        storeProduct = new StoreProduct();
        storeProduct.setUPC("123456789012");
        storeProduct.setUPC_prom(null);
        storeProduct.setId_product(1);
        storeProduct.setSelling_price(new BigDecimal("12.00"));
        storeProduct.setProducts_number(50);
        storeProduct.setPromotional_product(false);

        requestDto = new StoreProductRequestDto();
        requestDto.setUPC("123456789012");
        requestDto.setUPC_prom(null);
        requestDto.setId_product(1);
        requestDto.setPrice(new BigDecimal("10.00"));
        requestDto.setProducts_number(50);
        requestDto.setPromotional_product(false);

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
        storeProductDto.setSelling_price(new BigDecimal("12.00"));
    }

    @Test
    @DisplayName("findAll should return page of store products")
    void findAll_shouldReturnStoreProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong()))
                .thenReturn(List.of(storeProduct));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);
        when(mapper.toDto(any(StoreProduct.class))).thenReturn(storeProductDto);

        Page<StoreProductDto> result = repository.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("123456789012", result.getContent().get(0).getUPC());
    }

    @Test
    @DisplayName("findAllNoPagination should return list of store products")
    void findAllNoPagination_shouldReturnStoreProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper)))
                .thenReturn(List.of(storeProduct));
        when(mapper.toDto(any(StoreProduct.class))).thenReturn(storeProductDto);

        List<StoreProductDto> result = repository.findAllNoPagination();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456789012", result.get(0).getUPC());
    }

    @Test
    @DisplayName("findAllSortedByName should return page of products with names")
    void findAllSortedByName_shouldReturnProductsWithNames() {
        StoreProductWithNameDto dto = new StoreProductWithNameDto();
        dto.setUPC("123456789012");
        dto.setProduct_name("Test Product");

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class), anyInt(), anyLong()))
                .thenReturn(List.of(dto));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);

        Page<StoreProductWithNameDto> result = repository.findAllSortedByName(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getProduct_name());
    }

    @Test
    @DisplayName("findAllSortedByQuantity should return page of products sorted by quantity")
    void findAllSortedByQuantity_shouldReturnProductsSortedByQuantity() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong()))
                .thenReturn(List.of(storeProduct));
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(1);
        when(mapper.toDto(any(StoreProduct.class))).thenReturn(storeProductDto);

        Page<StoreProductDto> result = repository.findAllSortedByQuantity(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findPromotionalSortedByQuantity should return only promotional products")
    void findPromotionalSortedByQuantity_shouldReturnPromotionalProducts() {
        storeProduct.setPromotional_product(true);
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong()))
                .thenReturn(List.of(storeProduct));
        when(jdbcTemplate.queryForObject(
                eq("""
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = true AND is_deleted = false
                """),
                eq(Integer.class)))
                .thenReturn(1);
        when(mapper.toDto(any(StoreProduct.class))).thenReturn(storeProductDto);

        Page<StoreProductDto> result = repository.findPromotionalSortedByQuantity(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findNonPromotionalSortedByQuantity should return only non-promotional products")
    void findNonPromotionalSortedByQuantity_shouldReturnNonPromotionalProducts() {
        when(jdbcTemplate.query(anyString(), eq(rowMapper), anyInt(), anyLong()))
                .thenReturn(List.of(storeProduct));
        when(jdbcTemplate.queryForObject(
                eq("""
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = false AND is_deleted = false
                """),
                eq(Integer.class)))
                .thenReturn(1);
        when(mapper.toDto(any(StoreProduct.class))).thenReturn(storeProductDto);

        Page<StoreProductDto> result = repository.findNonPromotionalSortedByQuantity(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findPromotionalSortedByName should return promotional products sorted by name")
    void findPromotionalSortedByName_shouldReturnPromotionalProducts() {
        StoreProductWithNameDto dto = new StoreProductWithNameDto();
        dto.setUPC("123456789012");
        dto.setProduct_name("Test Product");
        dto.setPromotional_product(true);

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class),
                anyInt(), anyLong()))
                .thenReturn(List.of(dto));
        when(jdbcTemplate.queryForObject(
                eq("""
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = true AND is_deleted = false
                """),
                eq(Integer.class)))
                .thenReturn(1);

        Page<StoreProductWithNameDto> result = repository.findPromotionalSortedByName(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).isPromotional_product());
    }

    @Test
    @DisplayName("findNonPromotionalSortedByName should return non-promotional products sorted by name")
    void findNonPromotionalSortedByName_shouldReturnNonPromotionalProducts() {
        StoreProductWithNameDto dto = new StoreProductWithNameDto();
        dto.setUPC("123456789012");
        dto.setProduct_name("Test Product");
        dto.setPromotional_product(false);

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class),
                anyInt(), anyLong()))
                .thenReturn(List.of(dto));
        when(jdbcTemplate.queryForObject(
                eq("""
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = false AND is_deleted = false
                """),
                eq(Integer.class)))
                .thenReturn(1);

        Page<StoreProductWithNameDto> result = repository.findNonPromotionalSortedByName(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertFalse(result.getContent().get(0).isPromotional_product());
    }

    @Test
    @DisplayName("findAllInfoByUPC should return store product when exists")
    void findAllInfoByUPC_existingUPC_shouldReturnProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq("123456789012")))
                .thenReturn(storeProduct);

        Optional<StoreProduct> result = repository.findAllInfoByUPC("123456789012");

        assertTrue(result.isPresent());
        assertEquals("123456789012", result.get().getUPC());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(rowMapper), eq("123456789012"));
    }

    @Test
    @DisplayName("findAllInfoByUPC should return empty when product does not exist")
    void findAllInfoByUPC_nonExistingUPC_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq("999999999999")))
                .thenThrow(EmptyResultDataAccessException.class);

        Optional<StoreProduct> result = repository.findAllInfoByUPC("999999999999");

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(rowMapper), eq("999999999999"));
    }

    @Test
    @DisplayName("findByUPC should return characteristics when product exists")
    void findByUPC_existingUPC_shouldReturnCharacteristics() {
        StoreProductCharacteristicsDto dto = new StoreProductCharacteristicsDto();
        dto.setSelling_price(new BigDecimal("12.00"));
        dto.setProducts_number(50);
        dto.setProduct_name("Test Product");
        dto.setProduct_characteristics("Test characteristics");

        when(jdbcTemplate.queryForObject(anyString(), any(org.springframework.jdbc.core.RowMapper.class), eq("123456789012")))
                .thenReturn(dto);

        Optional<StoreProductCharacteristicsDto> result = repository.findByUPC("123456789012");

        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getProduct_name());
        assertEquals(new BigDecimal("12.00"), result.get().getSelling_price());
    }

    @Test
    @DisplayName("findByUPC should return empty when product does not exist")
    void findByUPC_nonExistingUPC_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(org.springframework.jdbc.core.RowMapper.class), eq("999999999999")))
                .thenThrow(EmptyResultDataAccessException.class);

        Optional<StoreProductCharacteristicsDto> result = repository.findByUPC("999999999999");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should return price and quantity")
    void findPriceAndQuantityByUPC_shouldReturnPriceAndQuantity() {
        StoreProductPriceAndQuantityDto dto = new StoreProductPriceAndQuantityDto();
        dto.setSelling_price(new BigDecimal("12.00"));
        dto.setProducts_number(50);

        when(jdbcTemplate.queryForObject(anyString(), any(org.springframework.jdbc.core.RowMapper.class), eq("123456789012")))
                .thenReturn(dto);

        Optional<StoreProductPriceAndQuantityDto> result = repository.findPriceAndQuantityByUPC("123456789012");

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("12.00"), result.get().getSelling_price());
        assertEquals(50, result.get().getProducts_number());
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should return empty when product does not exist")
    void findPriceAndQuantityByUPC_nonExistingUPC_shouldReturnEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(org.springframework.jdbc.core.RowMapper.class), eq("999999999999")))
                .thenThrow(EmptyResultDataAccessException.class);

        Optional<StoreProductPriceAndQuantityDto> result = repository.findPriceAndQuantityByUPC("999999999999");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save should insert new store product with VAT calculated")
    void save_newStoreProduct_shouldReturnSavedProduct() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(storeProduct);

        StoreProduct result = repository.save(requestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(rowMapper),
                anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("save should calculate promotional price correctly")
    void save_promotionalProduct_shouldCalculatePromotionalPrice() {
        requestDto.setPromotional_product(true);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(storeProduct);

        StoreProduct result = repository.save(requestDto);

        assertNotNull(result);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(rowMapper),
                anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    @DisplayName("save should throw InvalidProductException on data integrity violation")
    void save_invalidProduct_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper),
                anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidProductException.class, () -> repository.save(requestDto));
    }

    @Test
    @DisplayName("updateByUPC should update and return StoreProductDto when product exists")
    void updateByUPC_existingProduct_shouldReturnDto() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq("123456789012")))
                .thenReturn(storeProduct);
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = repository.updateByUPC("123456789012", requestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        verify(jdbcTemplate, times(1)).update(anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), eq("123456789012"));
    }

    @Test
    @DisplayName("updateByUPC should throw EntityNotFoundException when product does not exist")
    void updateByUPC_nonExistingProduct_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("999999999999")))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateByUPC("999999999999", requestDto));
    }

    @Test
    @DisplayName("updateByUPC should throw InvalidProductException on data integrity violation")
    void updateByUPC_invalidProduct_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), eq("123456789012")))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidProductException.class,
                () -> repository.updateByUPC("123456789012", requestDto));
    }

    @Test
    @DisplayName("updateByUPC should throw EntityNotFoundException when update affects 0 rows")
    void updateByUPC_updateFailed_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), eq("123456789012")))
                .thenReturn(0);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateByUPC("123456789012", requestDto));
    }

    @Test
    @DisplayName("updateByUPC should throw EntityNotFoundException when product not found after update")
    void updateByUPC_notFoundAfterUpdate_shouldThrowException() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), anyInt(), any(), anyInt(), anyBoolean(), eq("123456789012")))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), eq(rowMapper), eq("123456789012")))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThrows(EntityNotFoundException.class,
                () -> repository.updateByUPC("123456789012", requestDto));
    }

    @Test
    @DisplayName("softDeleteByUPC should mark product as deleted")
    void softDeleteByUPC_shouldMarkAsDeleted() {
        when(jdbcTemplate.update(anyString(), eq("123456789012"))).thenReturn(1);

        repository.softDeleteByUPC("123456789012");

        verify(jdbcTemplate, times(1)).update(anyString(), eq("123456789012"));
    }

    @Test
    @DisplayName("existsByUPC should return true when product exists")
    void existsByUPC_existingProduct_shouldReturnTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(1);

        assertTrue(repository.existsByUPC("123456789012"));
    }

    @Test
    @DisplayName("existsByUPC should return false when product does not exist")
    void existsByUPC_nonExistingProduct_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("999999999999")))
                .thenReturn(0);

        assertFalse(repository.existsByUPC("999999999999"));
    }

    @Test
    @DisplayName("existsByUPC should return false when count is null")
    void existsByUPC_nullCount_shouldReturnFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("123456789012")))
                .thenReturn(null);

        assertFalse(repository.existsByUPC("123456789012"));
    }

    @Test
    @DisplayName("updateProductPriceAndPromotion should update price and promotional status")
    void updateProductPriceAndPromotion_shouldUpdatePriceAndStatus() {
        BigDecimal newPrice = new BigDecimal("15.00");
        when(jdbcTemplate.update(anyString(), eq(newPrice), eq(true), eq("123456789012")))
                .thenReturn(1);

        repository.updateProductPriceAndPromotion("123456789012", newPrice, true);

        verify(jdbcTemplate, times(1))
                .update(anyString(), eq(newPrice), eq(true), eq("123456789012"));
    }
}
