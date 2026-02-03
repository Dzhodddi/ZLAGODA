package org.example.mapper.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        when(resultSet.getString("product_characteristics")).thenReturn("2.5% жирності, 1л");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(1, result.getId_product());
        assertEquals("Молоко", result.getProduct_name());
        assertEquals("2.5% жирності, 1л", result.getProduct_characteristics());
        assertEquals(10, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle null characteristics")
    void mapRow_nullCharacteristics_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Хліб");
        when(resultSet.getString("product_characteristics")).thenReturn(null);
        when(resultSet.getInt("category_number")).thenReturn(5);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(1, result.getId_product());
        assertEquals("Хліб", result.getProduct_name());
        assertNull(result.getProduct_characteristics());
        assertEquals(5, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle empty characteristics")
    void mapRow_emptyCharacteristics_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(2);
        when(resultSet.getString("product_name")).thenReturn("Вода");
        when(resultSet.getString("product_characteristics")).thenReturn("");
        when(resultSet.getInt("category_number")).thenReturn(15);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(2, result.getId_product());
        assertEquals("Вода", result.getProduct_name());
        assertEquals("", result.getProduct_characteristics());
        assertEquals(15, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should handle zero category number")
    void mapRow_zeroCategoryNumber_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(3);
        when(resultSet.getString("product_name")).thenReturn("Товар без категорії");
        when(resultSet.getString("product_characteristics")).thenReturn("Опис");
        when(resultSet.getInt("category_number")).thenReturn(0);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(0, result.getCategory_number());
    }

    @Test
    @DisplayName("mapRow should work with different row numbers")
    void mapRow_differentRowNumbers_shouldWork() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Молоко");
        when(resultSet.getString("product_characteristics")).thenReturn("2.5% жирності");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result1 = mapper.mapRow(resultSet, 0);
        Product result2 = mapper.mapRow(resultSet, 5);
        Product result3 = mapper.mapRow(resultSet, 100);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(1, result1.getId_product());
        assertEquals(1, result2.getId_product());
        assertEquals(1, result3.getId_product());
    }

    @Test
    @DisplayName("mapRow should handle long product name")
    void mapRow_longProductName_shouldHandleCorrectly() throws SQLException {
        String longName = "Дуже довга назва продукту яка містить багато символів і може бути обмежена базою даних";
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn(longName);
        when(resultSet.getString("product_characteristics")).thenReturn("Характеристики");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(longName, result.getProduct_name());
    }

    @Test
    @DisplayName("mapRow should handle long characteristics")
    void mapRow_longCharacteristics_shouldHandleCorrectly() throws SQLException {
        String longCharacteristics = "Дуже довгий опис характеристик продукту з багатьма деталями про склад, умови зберігання, термін придатності та іншу важливу інформацію";
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Продукт");
        when(resultSet.getString("product_characteristics")).thenReturn(longCharacteristics);
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(longCharacteristics, result.getProduct_characteristics());
    }

    @Test
    @DisplayName("mapRow should handle special characters in product name")
    void mapRow_specialCharactersInName_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Сир \"Моцарелла\" 45%");
        when(resultSet.getString("product_characteristics")).thenReturn("М'який сир");
        when(resultSet.getInt("category_number")).thenReturn(20);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals("Сир \"Моцарелла\" 45%", result.getProduct_name());
        assertEquals("М'який сир", result.getProduct_characteristics());
    }

    @Test
    @DisplayName("mapRow should create new Product instance each time")
    void mapRow_multipleCalls_shouldCreateNewInstances() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Молоко");
        when(resultSet.getString("product_characteristics")).thenReturn("2.5%");
        when(resultSet.getInt("category_number")).thenReturn(10);

        Product result1 = mapper.mapRow(resultSet, 0);
        Product result2 = mapper.mapRow(resultSet, 1);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertEquals(result1.getId_product(), result2.getId_product());
    }

    @Test
    @DisplayName("mapRow should handle large product ID")
    void mapRow_largeProductId_shouldHandleCorrectly() throws SQLException {
        when(resultSet.getInt("id_product")).thenReturn(999999);
        when(resultSet.getString("product_name")).thenReturn("Продукт");
        when(resultSet.getString("product_characteristics")).thenReturn("Опис");
        when(resultSet.getInt("category_number")).thenReturn(100);

        Product result = mapper.mapRow(resultSet, 0);

        assertNotNull(result);
        assertEquals(999999, result.getId_product());
    }
}
