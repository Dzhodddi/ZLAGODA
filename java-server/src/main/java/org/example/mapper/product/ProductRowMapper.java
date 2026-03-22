package org.example.mapper.product;

import org.example.model.product.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();

        ResultSetMetaData meta = rs.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columns.add(meta.getColumnName(i).toLowerCase());
        }

        if (columns.contains("id_product")) {
            product.setId_product(rs.getInt("id_product"));
        }
        if (columns.contains("product_name")) {
            product.setProduct_name(rs.getString("product_name"));
        }
        if (columns.contains("producer")) {
            product.setProducer(rs.getString("producer"));
        }
        if (columns.contains("product_characteristics")) {
            product.setProduct_characteristics(rs.getString("product_characteristics"));
        }
        if (columns.contains("category_number")) {
            product.setCategory_number(rs.getInt("category_number"));
        }
        if (columns.contains("category_name")) {
            product.setCategory_name(rs.getString("category_name"));
        }
        if (columns.contains("sold_number")) {
            product.setSold_number(rs.getInt("sold_number"));
        }
        if (columns.contains("total_sold")) {
            product.setTotal_sold(rs.getInt("total_sold"));
        }

        return product;
    }
}
