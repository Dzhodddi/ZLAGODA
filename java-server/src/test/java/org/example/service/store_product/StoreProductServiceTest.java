package org.example.service.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Store Product Service Tests")
class StoreProductServiceTest {

    @Mock
    private StoreProductRepository repository;

    @Mock
    private StoreProductMapper mapper;

    @InjectMocks
    private StoreProductServiceImpl service;

    private StoreProduct storeProduct;
    private StoreProductDto storeProductDto;
    private StoreProductRequestDto requestDto;
    private StoreProductWithNameDto withNameDto;
    private StoreProductCharacteristicsDto characteristicsDto;
    private StoreProductPriceAndQuantityDto priceAndQuantityDto;

    @BeforeEach
    void setUp() {
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
        requestDto.setPrice(new BigDecimal("10.00"));
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

    @Test
    @DisplayName("getAll without parameters should return all products")
    void getAll_noParameters_shouldReturnAllProducts() {
        when(repository.findAll()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<StoreProductDto> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456789012", result.get(0).getUPC());
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(storeProduct);
    }

    @Test
    @DisplayName("getAllSortedByQuantity should return products sorted by quantity")
    void getAllSortedByQuantity_shouldReturnSortedProducts() {
        when(repository.findAllSortedByQuantity()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<StoreProductDto> result = service.getAllSortedByQuantity();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAllSortedByQuantity();
    }

    @Test
    @DisplayName("getAllSortedByName should return products sorted by name")
    void getAllSortedByName_shouldReturnSortedProducts() {
        when(repository.findAllSortedByName()).thenReturn(List.of(withNameDto));

        List<StoreProductWithNameDto> result = service.getAllSortedByName();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getProduct_name());
        verify(repository, times(1)).findAllSortedByName();
    }

    @Test
    @DisplayName("getPromotionalSortedByQuantity should return promotional products")
    void getPromotionalSortedByQuantity_shouldReturnPromotionalProducts() {
        storeProduct.setPromotional_product(true);
        when(repository.findPromotionalSortedByQuantity()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<StoreProductDto> result = service.getPromotionalSortedByQuantity();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findPromotionalSortedByQuantity();
    }

    @Test
    @DisplayName("getNonPromotionalSortedByQuantity should return non-promotional products")
    void getNonPromotionalSortedByQuantity_shouldReturnNonPromotionalProducts() {
        when(repository.findNonPromotionalSortedByQuantity()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<StoreProductDto> result = service.getNonPromotionalSortedByQuantity();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findNonPromotionalSortedByQuantity();
    }

    @Test
    @DisplayName("getPromotionalSortedByName should return promotional products sorted by name")
    void getPromotionalSortedByName_shouldReturnPromotionalProducts() {
        when(repository.findPromotionalSortedByName()).thenReturn(List.of(withNameDto));

        List<StoreProductWithNameDto> result = service.getPromotionalSortedByName();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findPromotionalSortedByName();
    }

    @Test
    @DisplayName("getNonPromotionalSortedByName should return non-promotional products sorted by name")
    void getNonPromotionalSortedByName_shouldReturnNonPromotionalProducts() {
        when(repository.findNonPromotionalSortedByName()).thenReturn(List.of(withNameDto));

        List<StoreProductWithNameDto> result = service.getNonPromotionalSortedByName();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findNonPromotionalSortedByName();
    }

    @Test
    @DisplayName("getAll with sortedBy=name and prom=null should return all sorted by name")
    void getAll_sortByName_promNull_shouldReturnAllSortedByName() {
        when(repository.findAllSortedByName()).thenReturn(List.of(withNameDto));

        List<?> result = service.getAll("name", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAllSortedByName();
    }

    @Test
    @DisplayName("getAll with sortedBy=name and prom=true should return promotional sorted by name")
    void getAll_sortByName_promTrue_shouldReturnPromotionalSortedByName() {
        when(repository.findPromotionalSortedByName()).thenReturn(List.of(withNameDto));

        List<?> result = service.getAll("name", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findPromotionalSortedByName();
    }

    @Test
    @DisplayName("getAll with sortedBy=name and prom=false should return non-promotional sorted by name")
    void getAll_sortByName_promFalse_shouldReturnNonPromotionalSortedByName() {
        when(repository.findNonPromotionalSortedByName()).thenReturn(List.of(withNameDto));

        List<?> result = service.getAll("name", false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findNonPromotionalSortedByName();
    }

    @Test
    @DisplayName("getAll with sortedBy=quantity and prom=null should return all sorted by quantity")
    void getAll_sortByQuantity_promNull_shouldReturnAllSortedByQuantity() {
        when(repository.findAllSortedByQuantity()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<?> result = service.getAll("quantity", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAllSortedByQuantity();
    }

    @Test
    @DisplayName("getAll with sortedBy=quantity and prom=true should return promotional sorted by quantity")
    void getAll_sortByQuantity_promTrue_shouldReturnPromotionalSortedByQuantity() {
        when(repository.findPromotionalSortedByQuantity()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<?> result = service.getAll("quantity", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findPromotionalSortedByQuantity();
    }

    @Test
    @DisplayName("getAll with invalid sortedBy should return all products")
    void getAll_invalidSortedBy_shouldReturnAll() {
        when(repository.findAll()).thenReturn(List.of(storeProduct));
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        List<?> result = service.getAll("invalid", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("save should save and return StoreProductDto")
    void save_shouldReturnDto() {
        when(repository.save(requestDto)).thenReturn(storeProduct);
        when(mapper.toDto(storeProduct)).thenReturn(storeProductDto);

        StoreProductDto result = service.save(requestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        verify(repository, times(1)).save(requestDto);
        verify(mapper, times(1)).toDto(storeProduct);
    }

    @Test
    @DisplayName("updateByUPC should update and return StoreProductDto")
    void updateByUPC_shouldReturnUpdatedDto() {
        when(repository.updateByUPC("123456789012", requestDto)).thenReturn(storeProductDto);

        StoreProductDto result = service.updateByUPC("123456789012", requestDto);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
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
    @DisplayName("findByUPC should return characteristics when product exists")
    void findByUPC_existingProduct_shouldReturnCharacteristics() {
        when(repository.findByUPC("123456789012")).thenReturn(Optional.of(characteristicsDto));

        StoreProductCharacteristicsDto result = service.findByUPC("123456789012");

        assertNotNull(result);
        assertEquals("Test Product", result.getProduct_name());
        verify(repository, times(1)).findByUPC("123456789012");
    }

    @Test
    @DisplayName("findByUPC should throw EntityNotFoundException when product not found")
    void findByUPC_nonExistingProduct_shouldThrowException() {
        when(repository.findByUPC("999999999999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUPC("999999999999"));
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should return price and quantity when product exists")
    void findPriceAndQuantityByUPC_existingProduct_shouldReturnPriceAndQuantity() {
        when(repository.findPriceAndQuantityByUPC("123456789012"))
                .thenReturn(Optional.of(priceAndQuantityDto));

        StoreProductPriceAndQuantityDto result = service.findPriceAndQuantityByUPC("123456789012");

        assertNotNull(result);
        assertEquals(new BigDecimal("12.00"), result.getSelling_price());
        assertEquals(50, result.getProducts_number());
        verify(repository, times(1)).findPriceAndQuantityByUPC("123456789012");
    }

    @Test
    @DisplayName("findPriceAndQuantityByUPC should throw EntityNotFoundException when product not found")
    void findPriceAndQuantityByUPC_nonExistingProduct_shouldThrowException() {
        when(repository.findPriceAndQuantityByUPC("999999999999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findPriceAndQuantityByUPC("999999999999"));
    }
}
