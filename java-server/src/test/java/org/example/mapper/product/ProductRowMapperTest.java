package org.example.mapper.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Row Mapper Tests")
class ProductRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ProductRowMapper mapper;

    @Test
    @DisplayName("mapRow should map all product fields correctly")
    void mapRow_allFields_shouldMapCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Молоко");
        when(resultSet.getString("producer")).thenReturn("Ферма А");
        when(resultSet.getString("product_characteristics")).thenReturn("2.5% жирності, 1л");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(1, result.getId_product());
        assertEquals("Молоко", result.getProduct_name());
        assertEquals("Ферма А", result.getProducer());
        assertEquals("2.5% жирності, 1л", result.getProduct_characteristics());
        assertEquals(10, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle null characteristics")
    void mapRow_nullCharacteristics_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Хліб");
        when(resultSet.getString("producer")).thenReturn("Пекарня Б");
        when(resultSet.getString("product_characteristics")).thenReturn(null);
        when(resultSet.getInt("category_number")).thenReturn(5);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(1, result.getId_product());
        assertEquals("Хліб", result.getProduct_name());
        assertEquals("Пекарня Б", result.getProducer());
        assertNull(result.getProduct_characteristics());
        assertEquals(5, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle empty characteristics")
    void mapRow_emptyCharacteristics_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(2);
        when(resultSet.getString("product_name")).thenReturn("Вода");
        when(resultSet.getString("producer")).thenReturn("Виробник В");
        when(resultSet.getString("product_characteristics")).thenReturn("");
        when(resultSet.getInt("category_number")).thenReturn(15);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(2, result.getId_product());
        assertEquals("Вода", result.getProduct_name());
        assertEquals("Виробник В", result.getProducer());
        assertEquals("", result.getProduct_characteristics());
        assertEquals(15, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle zero category number")
    void mapRow_zeroCategoryNumber_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(3);
        when(resultSet.getString("product_name")).thenReturn("Товар без категорії");
        when(resultSet.getString("producer")).thenReturn("Виробник Г");
        when(resultSet.getString("product_characteristics")).thenReturn("Опис");
        when(resultSet.getInt("category_number")).thenReturn(0);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals("Виробник Г", result.getProducer());
        assertEquals(0, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should create new Product instance each time")
    void mapRow_multipleCalls_shouldCreateNewInstances() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Молоко");
        when(resultSet.getString("producer")).thenReturn("Ферма А");
        when(resultSet.getString("product_characteristics")).thenReturn("2.5%");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result1 = mapper.mapRow(resultSet, 0);
        Product result2 = mapper.mapRow(resultSet, 1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getId_product(), result2.getId_product());
        assertEquals(result1.getProducer(), result2.getProducer());
    }
}
