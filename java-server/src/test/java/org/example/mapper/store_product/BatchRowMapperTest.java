package org.example.mapper.store_product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.store_product.Batch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Batch Row Mapper Tests")
class BatchRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BatchRowMapper mapper;

    private Date deliveryDate;
    private Date expiringDate;

    @BeforeEach
    void setUp() {
        deliveryDate = Date.valueOf("2024-01-01");
        expiringDate = Date.valueOf("2024-02-01");
    }

    @Test
    @DisplayName("mapRow should map all batch fields correctly")
    void mapRow_allFields_shouldMapCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(100);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("12.50"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123456789012", result.getUPC());
        assertEquals(deliveryDate, result.getDelivery_date());
        assertEquals(expiringDate, result.getExpiring_date());
        assertEquals(100, result.getQuantity());
        assertEquals(new BigDecimal("12.50"), result.getSelling_price());
    }

    @Test
    @DisplayName("mapRow should handle zero quantity")
    void mapRow_zeroQuantity_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(0);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(0, result.getQuantity());
    }

    @Test
    @DisplayName("mapRow should handle large quantity")
    void mapRow_largeQuantity_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(999999);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("5.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(999999, result.getQuantity());
    }

    @Test
    @DisplayName("mapRow should handle same delivery and expiring dates")
    void mapRow_sameDates_shouldHandleCorrectly() throws SQLException {
        Date sameDate = Date.valueOf("2024-01-01");
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(sameDate);
        when(resultSet.getDate("expiring_date")).thenReturn(sameDate);
        when(resultSet.getInt("quantity")).thenReturn(50);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(sameDate, result.getDelivery_date());
        assertEquals(sameDate, result.getExpiring_date());
    }

    @Test
    @DisplayName("mapRow should handle zero price")
    void mapRow_zeroPrice_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(50);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(BigDecimal.ZERO);

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getSelling_price());
    }

    @Test
    @DisplayName("mapRow should handle price with many decimal places")
    void mapRow_priceWithManyDecimals_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(50);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("12.999999"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(new BigDecimal("12.999999"), result.getSelling_price());
    }

    @Test
    @DisplayName("mapRow should handle large ID")
    void mapRow_largeId_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(9999999999L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(50);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(9999999999L, result.getId());
    }

    @Test
    @DisplayName("mapRow should work with different row numbers")
    void mapRow_differentRowNumbers_shouldWork() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(100);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result1 = mapper.mapRow(resultSet, 0);
        Batch result2 = mapper.mapRow(resultSet, 5);
        Batch result3 = mapper.mapRow(resultSet, 100);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(1L, result1.getId());
        assertEquals(1L, result2.getId());
        assertEquals(1L, result3.getId());
    }

    @Test
    @DisplayName("mapRow should create new Batch instance each time")
    void mapRow_multipleCalls_shouldCreateNewInstances() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(100);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result1 = mapper.mapRow(resultSet, 0);
        Batch result2 = mapper.mapRow(resultSet, 1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getId(), result2.getId());
    }

    @Test
    @DisplayName("mapRow should handle old delivery date")
    void mapRow_oldDeliveryDate_shouldHandleCorrectly() throws SQLException {
        Date oldDate = Date.valueOf("2020-01-01");
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(oldDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(100);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(oldDate, result.getDelivery_date());
    }

    @Test
    @DisplayName("mapRow should handle future expiring date")
    void mapRow_futureExpiringDate_shouldHandleCorrectly() throws SQLException {
        Date futureDate = Date.valueOf("2030-12-31");
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(futureDate);
        when(resultSet.getInt("quantity")).thenReturn(100);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("10.00"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(futureDate, result.getExpiring_date());
    }

    @Test
    @DisplayName("mapRow should handle very high price")
    void mapRow_veryHighPrice_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("UPC")).thenReturn("123456789012");
        when(resultSet.getDate("delivery_date")).thenReturn(deliveryDate);
        when(resultSet.getDate("expiring_date")).thenReturn(expiringDate);
        when(resultSet.getInt("quantity")).thenReturn(1);
        when(resultSet.getBigDecimal("selling_price")).thenReturn(new BigDecimal("999999.99"));

        Batch result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(new BigDecimal("999999.99"), result.getSelling_price());
    }
}
