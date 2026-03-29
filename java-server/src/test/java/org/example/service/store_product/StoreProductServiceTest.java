package org.example.service.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.model.store_product.StoreProduct;
import org.example.repository.store_product.StoreProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Store Product Service Tests")
class StoreProductServiceTest {

    @Mock private StoreProductRepository repository;
    @Mock private StoreProductMapper mapper;

    @InjectMocks
    private StoreProductServiceImpl service;

    private StoreProduct storeProduct;
    private StoreProductDto storeProductDto;
    private StoreProductRequestDto requestDto;
    private StoreProductWithNameDto withNameDto;
    private StoreProductCharacteristicsDto characteristicsDto;
    private StoreProductPriceAndQuantityDto priceAndQuantityDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        storeProduct = new StoreProduct();
        storeProduct.setUPC("123456789012");
        storeProduct.setId_product(1);
        storeProduct.setSelling_price(new BigDecimal("12.00"));
        storeProduct.setProducts_number(50);
        storeProduct.setPromotional_product(false);

        storeProductDto = new StoreProductDto();
        storeProductDto.setUPC("123456789012");
        storeProductDto.setSelling_price(new BigDecimal("12.00"));
        storeProductDto.setProducts_number(50);

        requestDto = new StoreProductRequestDto();
        requestDto.setUPC("123456789012");
        requestDto.setId_product(1);
        requestDto.setSelling_price(new BigDecimal("10.00"));
        requestDto.setProducts_number(50);
        requestDto.setPromotional_product(false);

        withNameDto = new StoreProductWithNameDto();
        withNameDto.setUPC("123456789012");
        withNameDto.setProduct_name("Test Product");

        characteristicsDto = new StoreProductCharacteristicsDto();
        characteristicsDto.setSelling_price(new BigDecimal("12.00"));
        characteristicsDto.setProducts_number(50);
        characteristicsDto.setProduct_name("Test Product");

        priceAndQuantityDto = new StoreProductPriceAndQuantityDto();
        priceAndQuantityDto.setSelling_price(new BigDecimal("12.00"));
        priceAndQuantityDto.setProducts_number(50);
    }

    private <T> PageResponseDto<T> page(T item) {
        return PageResponseDto.of(List.of(item), 10, 1, false);
    }

    @Test
    @DisplayName("getAll with Pageable should return all products")
    void getAll_withPageable_shouldReturnAllProducts() {
        when(repository.findAll(eq(pageable))).thenReturn(page(withNameDto));

        PageResponseDto<?> result = service.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    @DisplayName("getAllSortedByQuantity should return products sorted by quantity")
    void getAllSortedByQuantity_shouldReturnSortedProducts() {
        when(repository.findAllSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAllSortedByQuantity(pageable).getContent().size());
        verify(repository, times(1)).findAllSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getAll with invalid sortedBy should return all products")
    void getAll_invalidSortedBy_shouldReturnAll() {
        when(repository.findAll(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("invalid", null, pageable).getContent().size());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    @DisplayName("findByUPC should return characteristics when product exists")
    void findByUPC_existingProduct_shouldReturnCharacteristics() {
        when(repository.findByUPC("123456789012")).thenReturn(Optional.of(withNameDto));

        assertEquals("Test Product", service.findByUPC("123456789012").getProduct_name());
        verify(repository, times(1)).findByUPC("123456789012");
    }

    @Test
    @DisplayName("getAllNoPagination should return all products without pagination")
    void getAllNoPagination_shouldReturnAllProducts() {
        when(repository.findAllNoPagination()).thenReturn(List.of(storeProductDto));

        List<StoreProductDto> result = service.getAllNoPagination();

        assertEquals(1, result.size());
        verify(repository, times(1)).findAllNoPagination();
    }

    @Test
    @DisplayName("getAllSortedByName should return products sorted by name")
    void getAllSortedByName_shouldReturnSortedProducts() {
        when(repository.findAllSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        PageResponseDto<StoreProductWithNameDto> result = service.getAllSortedByName(pageable);

        assertEquals("Test Product", result.getContent().get(0).getProduct_name());
        verify(repository, times(1)).findAllSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("getPromotionalSortedByName should return promotional products sorted by name")
    void getPromotionalSortedByName_shouldReturnPromotionalProducts() {
        when(repository.findPromotionalSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getPromotionalSortedByName(pageable).getContent().size());
        verify(repository, times(1)).findPromotionalSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("getNonPromotionalSortedByName should return non-promotional products sorted by name")
    void getNonPromotionalSortedByName_shouldReturnNonPromotionalProducts() {
        when(repository.findNonPromotionalSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getNonPromotionalSortedByName(pageable).getContent().size());
        verify(repository, times(1)).findNonPromotionalSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=name prom=null should return all sorted by name")
    void getAll_sortByName_promNull() {
        when(repository.findAllSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("name", null, pageable).getContent().size());
        verify(repository, times(1)).findAllSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=name prom=true should return promotional sorted by name")
    void getAll_sortByName_promTrue() {
        when(repository.findPromotionalSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("name", true, pageable).getContent().size());
        verify(repository, times(1)).findPromotionalSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=name prom=false should return non-promotional sorted by name")
    void getAll_sortByName_promFalse() {
        when(repository.findNonPromotionalSortedByName(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("name", false, pageable).getContent().size());
        verify(repository, times(1)).findNonPromotionalSortedByName(eq(pageable));
    }

    @Test
    @DisplayName("save should save and return StoreProductDto")
    void save_shouldReturnDto() {
        when(repository.save(requestDto)).thenReturn(storeProduct);
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = service.save(requestDto);

        assertEquals("123456789012", result.getUPC());
        verify(repository, times(1)).save(requestDto);
    }

    @Test
    @DisplayName("updateByUPC should update and return StoreProductDto")
    void updateByUPC_shouldReturnUpdatedDto() {
        when(repository.updateByUPC("123456789012", requestDto)).thenReturn(storeProductDto);

        assertEquals("123456789012", service.updateByUPC("123456789012", requestDto).getUPC());
        verify(repository, times(1)).updateByUPC("123456789012", requestDto);
    }

    @Test
    @DisplayName("softDeleteByUPC should call repository softDelete")
    void softDeleteByUPC_shouldCallRepository() {
        doNothing().when(repository).softDeleteByUPC("123456789012");

        service.softDeleteByUPC("123456789012");

        verify(repository, times(1)).softDeleteByUPC("123456789012");
    }

    @Test
    @DisplayName("findByUPC should throw EntityNotFoundException when product not found")
    void findByUPC_nonExistingProduct_shouldThrowException() {
        when(repository.findByUPC("999999999999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUPC("999999999999"));
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should return price and quantity when product exists")
    void findPriceAndQuantityByUPC_existingProduct_shouldReturn() {
        when(repository.findPriceAndQuantityByUPC("123456789012"))
                .thenReturn(Optional.of(priceAndQuantityDto));

        StoreProductPriceAndQuantityDto result =
                service.findPriceAndQuantityByUPC("123456789012");

        assertEquals(new BigDecimal("12.00"), result.getSelling_price());
        verify(repository, times(1)).findPriceAndQuantityByUPC("123456789012");
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should throw EntityNotFoundException when not found")
    void findPriceAndQuantityByUPC_nonExistingProduct_shouldThrowException() {
        when(repository.findPriceAndQuantityByUPC("999999999999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findPriceAndQuantityByUPC("999999999999"));
    }

    @Test
    @DisplayName("getAll sortedBy=quantity prom=null should return all sorted by quantity")
    void getAll_sortByQuantity_promNull() {
        when(repository.findAllSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("quantity", null, pageable).getContent().size());
        verify(repository).findAllSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=quantity prom=true should return promotional sorted by quantity")
    void getAll_sortByQuantity_promTrue() {
        when(repository.findPromotionalSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("quantity", true, pageable).getContent().size());
        verify(repository).findPromotionalSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=quantity prom=false should return non-promotional sorted by quantity")
    void getAll_sortByQuantity_promFalse() {
        when(repository.findNonPromotionalSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll("quantity", false, pageable).getContent().size());
        verify(repository).findNonPromotionalSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=null prom=true should return promotional products")
    void getAll_noSort_promTrue() {
        when(repository.findPromotional(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll(null, true, pageable).getContent().size());
        verify(repository).findPromotional(eq(pageable));
    }

    @Test
    @DisplayName("getAll sortedBy=null prom=false should return non-promotional products")
    void getAll_noSort_promFalse() {
        when(repository.findNonPromotional(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getAll(null, false, pageable).getContent().size());
        verify(repository).findNonPromotional(eq(pageable));
    }

    @Test
    @DisplayName("getPromotional should return promotional products")
    void getPromotional_shouldReturnProducts() {
        when(repository.findPromotional(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getPromotional(pageable).getContent().size());
        verify(repository).findPromotional(eq(pageable));
    }

    @Test
    @DisplayName("getNonPromotional should return non-promotional products")
    void getNonPromotional_shouldReturnProducts() {
        when(repository.findNonPromotional(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getNonPromotional(pageable).getContent().size());
        verify(repository).findNonPromotional(eq(pageable));
    }

    @Test
    @DisplayName("getPromotionalSortedByQuantity should return promotional products sorted by quantity")
    void getPromotionalSortedByQuantity_shouldReturnProducts() {
        when(repository.findPromotionalSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getPromotionalSortedByQuantity(pageable).getContent().size());
        verify(repository).findPromotionalSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getNonPromotionalSortedByQuantity should return non-promotional products sorted by quantity")
    void getNonPromotionalSortedByQuantity_shouldReturnProducts() {
        when(repository.findNonPromotionalSortedByQuantity(eq(pageable))).thenReturn(page(withNameDto));

        assertEquals(1, service.getNonPromotionalSortedByQuantity(pageable).getContent().size());
        verify(repository).findNonPromotionalSortedByQuantity(eq(pageable));
    }

    @Test
    @DisplayName("getAllWithNameNoPagination should return all products with names")
    void getAllWithNameNoPagination_shouldReturnAllProducts() {
        when(repository.findAllWithNameNoPagination()).thenReturn(List.of(withNameDto));

        List<StoreProductWithNameDto> result = service.getAllWithNameNoPagination();

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getProduct_name());
        verify(repository).findAllWithNameNoPagination();
    }
}
