package org.example.mapper.store_product;

import org.example.model.store_product.Batch;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BatchRowMapper implements RowMapper<Batch> {

    @Override
    public Batch mapRow(ResultSet rs, int rowNum) throws SQLException {
        Batch batch = new Batch();
        batch.setId(rs.getLong("id"));
        batch.setUPC(rs.getString("UPC"));
        batch.setDelivery_date(rs.getDate("delivery_date"));
        batch.setExpiring_date(rs.getDate("expiring_date"));
        batch.setQuantity(rs.getInt("quantity"));
        batch.setSelling_price(rs.getBigDecimal("selling_price"));
        return batch;
    }
}
