package org.example.mapper.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.store_product.StoreProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Store Product Row Mapper Tests")
class StoreProductRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private StoreProductRowMapper mapper;

    @Test
    @DisplayName("mapRow should map all store product fields correctly")
    void mapRow_allFields_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn("987654321098");
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("12.50"));
        when(resultSet.getInt("products_number")).thenReturn(50);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals("123456789012", result.getUPC());
        assertEquals("987654321098", result.getUPC_prom());
        assertEquals(1, result.getId_product());
        assertEquals(new BigDecimal("12.50"), result.getSelling_price());
        assertEquals(50, result.getProducts_number());
        assertFalse(result.isPromotional_product());
    }

    @Test
    @DisplayName("mapRow should map promotional product correctly")
    void mapRow_promotionalProduct_shouldMapCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("111111111111");
        when(resultSet.getString("UPC_prom")).thenReturn("222222222222");
        when(resultSet.getInt("id_product")).thenReturn(2);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("9.99"));
        when(resultSet.getInt("products_number")).thenReturn(30);
        when(resultSet.getBoolean("promotional_product")).thenReturn(true);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertTrue(result.isPromotional_product());
        assertEquals("222222222222", result.getUPC_prom());
    }

    @Test
    @DisplayName("mapRow should handle null promotional UPC")
    void mapRow_nullPromotionalUPC_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("15.00"));
        when(resultSet.getInt("products_number")).thenReturn(40);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertNull(result.getUPC_prom());
        assertFalse(result.isPromotional_product());
    }

    @Test
    @DisplayName("mapRow should handle zero quantity")
    void mapRow_zeroQuantity_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getInt("products_number")).thenReturn(0);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(0, result.getProducts_number());
    }

    @Test
    @DisplayName("mapRow should handle large quantity")
    void mapRow_largeQuantity_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("5.00"));
        when(resultSet.getInt("products_number")).thenReturn(10000);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(10000, result.getProducts_number());
    }

    @Test
    @DisplayName("mapRow should handle zero price")
    void mapRow_zeroPrice_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(BigDecimal.ZERO);
        when(resultSet.getInt("products_number")).thenReturn(10);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getSelling_price());
    }

    @Test
    @DisplayName("mapRow should handle price with many decimal places")
    void mapRow_priceWithManyDecimals_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("12.999999"));
        when(resultSet.getInt("products_number")).thenReturn(10);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(new BigDecimal("12.999999"), result.getSelling_price());
    }

    @Test
    @DisplayName("mapRow should work with different row numbers")
    void mapRow_differentRowNumbers_shouldWork() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getInt("products_number")).thenReturn(50);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result1 = mapper.mapRow(resultSet, 0);
        StoreProduct result2 = mapper.mapRow(resultSet, 5);
        StoreProduct result3 = mapper.mapRow(resultSet, 100);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals("123456789012", result1.getUPC());
        assertEquals("123456789012", result2.getUPC());
        assertEquals("123456789012", result3.getUPC());
    }

    @Test
    @DisplayName("mapRow should create new StoreProduct instance each time")
    void mapRow_multipleCalls_shouldCreateNewInstances() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getInt("products_number")).thenReturn(50);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result1 = mapper.mapRow(resultSet, 0);
        StoreProduct result2 = mapper.mapRow(resultSet, 1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getUPC(), result2.getUPC());
    }

    @Test
    @DisplayName("mapRow should handle promotional product with null promotional UPC")
    void mapRow_promotionalProductNullUPC_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("8.00"));
        when(resultSet.getInt("products_number")).thenReturn(15);
        when(resultSet.getBoolean("promotional_product")).thenReturn(true);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertTrue(result.isPromotional_product());
        assertNull(result.getUPC_prom());
    }

    @Test
    @DisplayName("mapRow should handle very high price")
    void mapRow_veryHighPrice_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getString("UPC_prom")).thenReturn(null);
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("9999999.99"));
        when(resultSet.getInt("products_number")).thenReturn(1);
        when(resultSet.getBoolean("promotional_product")).thenReturn(false);

        StoreProduct result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(new BigDecimal("9999999.99"), result.getSelling_price());
    }
}
