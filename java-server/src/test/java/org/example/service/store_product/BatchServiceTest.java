package org.example.service.store_product;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.model.store_product.StoreProduct;
import org.example.repository.store_product.BatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Batch Service Tests")
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private StoreProductMapper storeProductMapper;

    @InjectMocks
    private BatchServiceImpl service;

    private BatchRequestDto batchRequestDto;
    private StoreProduct storeProduct;
    private StoreProductDto storeProductDto;

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
        storeProduct.setProducts_number(70);
        storeProduct.setPromotional_product(false);

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
        storeProductDto.setSelling_price(new BigDecimal("12.00"));
        storeProductDto.setProducts_number(70);
    }

    @Test
    @DisplayName("save should save batch and return StoreProductDto")
    void save_validBatch_shouldReturnDto() {
        when(batchRepository.save(batchRequestDto)).thenReturn(storeProduct);
        when(storeProductMapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = service.save(batchRequestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        assertEquals(70, result.getProducts_number());
        verify(batchRepository, times(1)).save(batchRequestDto);
        verify(storeProductMapper, times(1)).toDto(storeProduct);
    }

    @Test
    @DisplayName("save should handle promotional batch")
    void save_promotionalBatch_shouldReturnDto() {
        LocalDate expiringDate = LocalDate.now().plusDays(3);
        batchRequestDto.setExpiring_date(Date.from(expiringDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        storeProduct.setPromotional_product(true);

        when(batchRepository.save(batchRequestDto)).thenReturn(storeProduct);
        when(storeProductMapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = service.save(batchRequestDto);

        assertNotNull(result);
        verify(batchRepository, times(1)).save(batchRequestDto);
        verify(storeProductMapper, times(1)).toDto(storeProduct);
    }

    @Test
    @DisplayName("removeExpired should call repository deleteExpired")
    void removeExpired_shouldCallRepository() {
        doNothing().when(batchRepository).deleteExpired();

        service.removeExpired();

        verify(batchRepository, times(1)).deleteExpired();
    }

    @Test
    @DisplayName("removeExpired should not throw exception")
    void removeExpired_shouldNotThrowException() {
        doNothing().when(batchRepository).deleteExpired();

        assertDoesNotThrow(() -> service.removeExpired());
    }

    @Test
    @DisplayName("save should properly map StoreProduct to Dto")
    void save_shouldMapCorrectly() {
        when(batchRepository.save(any(BatchRequestDto.class))).thenReturn(storeProduct);
        when(storeProductMapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = service.save(batchRequestDto);

        assertNotNull(result);
        assertEquals(storeProduct.getUPC(), result.getUPC());
        assertEquals(storeProduct.getSelling_price(), result.getSelling_price());
    }
}
