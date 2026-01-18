package org.example.mapper.product;

import org.example.model.product.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setIdProduct(rs.getLong("id_product"));
        product.setProductName(rs.getString("product_name"));
        product.setProductCharacteristics(rs.getString("product_characteristics"));
        product.setCategoryNumber(rs.getInt("category_number"));
        return product;
    }
}
