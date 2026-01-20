package org.example.mapper.store_product;

import org.example.model.store_product.StoreProduct;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StoreProductRowMapper implements RowMapper<StoreProduct> {

    @Override
    public StoreProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setUPC(rs.getString("UPC"));
        storeProduct.setUPC_prom(rs.getString("UPC_prom"));
        storeProduct.setId_product(rs.getInt("id_product"));
        storeProduct.setSelling_price(rs.getBigDecimal("selling_price"));
        storeProduct.setProducts_number(rs.getInt("products_number"));
        storeProduct.setPromotional_product(rs.getBoolean("promotional_product"));
        return storeProduct;
    }
}
