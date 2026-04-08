package org.example.repository.store_product;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.model.store_product.StoreProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
                .thenReturn(Optional.of(storeProduct));

        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyInt(), any()))
                .thenReturn(1);
        when(jdbcTemplate.update(contains("SET products_number"), anyInt(), anyString()))
                .thenReturn(1);

        StoreProduct result = repository.save(batchRequestDto);

        assertNotNull(result);
        verify(jdbcTemplate).update(
                contains("INSERT INTO batch"),
                eq("123456789012"),
                any(),
                any(),
                eq(20),
                eq(new BigDecimal("12.00"))
        );

        verify(jdbcTemplate).update(
                contains("SET products_number"),
                eq(70),
                eq("123456789012")
        );
    }

    @Test
    @DisplayName("save should calculate VAT correctly")
    void save_shouldCalculateVAT() {
        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct));

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
    @DisplayName("save should handle promotional logic when expiring soon")
    void save_expiringBatch_shouldSetPromotional() {
        LocalDate expiringSoon = LocalDate.now().plusDays(2);
        batchRequestDto.setExpiring_date(Date.from(expiringSoon.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        batchRequestDto.setQuantity(15);

        when(storeProductRepository.findAllInfoByUPC(eq("123456789012")))
                .thenReturn(Optional.of(storeProduct));
        when(storeProductRepository.findPromById_Product(1)).thenReturn(Optional.empty());

        repository.save(batchRequestDto);

        verify(jdbcTemplate).update(contains("INSERT INTO store_product"), anyString(), any(), eq(1), any(), eq(15), eq(true));
        verify(jdbcTemplate).update(contains("SET UPC_prom"), anyString(), eq("123456789012"));
    }

    @Test
    @DisplayName("save should throw EntityNotFoundException when store product not found")
    void save_storeProductNotFound_shouldThrowException() {
        when(storeProductRepository.findAllInfoByUPC(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repository.save(batchRequestDto));
    }
}
