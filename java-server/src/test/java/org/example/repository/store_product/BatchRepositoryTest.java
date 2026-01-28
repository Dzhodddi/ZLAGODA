package org.example.repository.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidProductException;
import org.example.model.store_product.StoreProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Batch Repository Tests")
class BatchRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private StoreProductRepository storeProductRepository;

    @InjectMocks
    private BatchRepository repository;

    private BatchRequestDto batchRequestDto;
    private StoreProduct storeProduct;

    @BeforeEach
    void setUp() {
        LocalDate deliveryDate = LocalDate.now();
        LocalDate expiringDate = LocalDate.now().plusDays(30);

        batchRequestDto = new BatchRequestDto();
        batchRequestDto.setUPC("123456789012");
        batchRequestDto.setDelivery_date(Date.from(deliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(20);
        batchRequestDto.setPrice(new BigDecimal("10.00"));

        storeProduct = new StoreProduct();
        storeProduct.setUPC("123456789012");
        storeProduct.setId_product(1);
        storeProduct.setSelling_price(new BigDecimal("12.00"));
        storeProduct.setProducts_number(50);
        storeProduct.setPromotional_product(false);
    }

    @Test
    @DisplayName("save should insert batch and update store product quantity")
    void save_validBatch_shouldInsertAndUpdateQuantity() {
        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), anyString()))
                .thenReturn(1);
        when(jdbcTemplate.update(anyString(), anyInt(), anyString()))
                .thenReturn(1);

        StoreProduct result = repository.save(batchRequestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        verify(jdbcTemplate, times(1)).update(
                contains("INSERT INTO batch"),
                eq("123456789012"),
                any(),
                any(),
                eq(20),
                any()
        );
        verify(jdbcTemplate, times(1)).update(
                contains("UPDATE store_product SET products_number"),
                eq(70),
                eq("123456789012")
        );
    }

    @Test
    @DisplayName("save should calculate VAT correctly")
    void save_shouldCalculateVAT() {
        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        repository.save(batchRequestDto);

        verify(jdbcTemplate).update(
                anyString(),
                eq("123456789012"),
                any(),
                any(),
                eq(20),
                eq(new BigDecimal("12.00"))
        );
    }

    @Test
    @DisplayName("save should set promotional when quantity >= 10 and expiring soon")
    void save_expiringBatch_shouldSetPromotional() {
        LocalDate expiringDate = LocalDate.now().plusDays(3);
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(15);

        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        repository.save(batchRequestDto);

        verify(storeProductRepository, times(1)).updateProductPriceAndPromotion(
                eq("123456789012"),
                any(BigDecimal.class),
                eq(true)
        );
    }

    @Test
    @DisplayName("save should not set promotional when quantity < 10 even if expiring soon")
    void save_lowQuantityExpiring_shouldNotSetPromotional() {
        LocalDate expiringDate = LocalDate.now().plusDays(3);
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(5);
        storeProduct.setProducts_number(3);

        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        repository.save(batchRequestDto);

        verify(storeProductRepository, times(1)).updateProductPriceAndPromotion(
                eq("123456789012"),
                any(BigDecimal.class),
                eq(false)
        );
    }

    @Test
    @DisplayName("save should not set promotional when expiring date > 5 days")
    void save_notExpiringYet_shouldNotSetPromotional() {
        LocalDate expiringDate = LocalDate.now().plusDays(10);
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(15);

        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        repository.save(batchRequestDto);

        verify(storeProductRepository, times(1)).updateProductPriceAndPromotion(
                eq("123456789012"),
                any(BigDecimal.class),
                eq(false)
        );
    }

    @Test
    @DisplayName("save should throw EntityNotFoundException when store product not found")
    void save_storeProductNotFound_shouldThrowException() {
        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.empty());
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        assertThrows(EntityNotFoundException.class, () -> repository.save(batchRequestDto));
    }

    @Test
    @DisplayName("save should throw InvalidProductException on data integrity violation")
    void save_invalidProduct_shouldThrowException() {
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(InvalidProductException.class, () -> repository.save(batchRequestDto));
    }

    @Test
    @DisplayName("deleteExpired should delete expired batches and update quantities")
    void deleteExpired_shouldDeleteExpiredBatches() {
        Object[] expiredBatch1 = {"123456789012", 10};
        Object[] expiredBatch2 = {"987654321098", 5};

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of(expiredBatch1, expiredBatch2));
        when(jdbcTemplate.update(contains("UPDATE store_product"), anyInt(), anyString()))
                .thenReturn(1);
        when(jdbcTemplate.update(contains("DELETE FROM batch")))
                .thenReturn(2);

        repository.deleteExpired();

        verify(jdbcTemplate, times(1)).update(
                contains("UPDATE store_product SET products_number"),
                eq(10),
                eq("123456789012")
        );
        verify(jdbcTemplate, times(1)).update(
                contains("UPDATE store_product SET products_number"),
                eq(5),
                eq("987654321098")
        );
        verify(jdbcTemplate, times(1)).update(contains("DELETE FROM batch"));
    }

    @Test
    @DisplayName("deleteExpired should handle empty expired batches")
    void deleteExpired_noExpiredBatches_shouldNotUpdateQuantities() {
        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of());
        when(jdbcTemplate.update(contains("DELETE FROM batch")))
                .thenReturn(0);

        repository.deleteExpired();

        verify(jdbcTemplate, never()).update(contains("UPDATE store_product"), anyInt(), anyString());
        verify(jdbcTemplate, times(1)).update(contains("DELETE FROM batch"));
    }

    @Test
    @DisplayName("save should apply promotional discount correctly")
    void save_promotionalBatch_shouldApplyDiscount() {
        LocalDate expiringDate = LocalDate.now().plusDays(3);
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(15);
        batchRequestDto.setPrice(new BigDecimal("10.00"));

        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct))
                .thenReturn(Optional.of(storeProduct));
        when(jdbcTemplate.update(anyString(), anyString(), any(), any(), anyInt(), any()))
                .thenReturn(1);

        repository.save(batchRequestDto);

        // Price with VAT = 10.00 * 1.20 = 12.00
        // Promotional price = 12.00 * 0.8 = 9.60
        verify(storeProductRepository, times(1)).updateProductPriceAndPromotion(
                eq("123456789012"),
                eq(new BigDecimal("9.60")),
                eq(true)
        );
    }
}
