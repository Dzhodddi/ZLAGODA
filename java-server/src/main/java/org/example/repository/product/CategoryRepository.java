package org.example.repository.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CategoryResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CategoryResponseDto> fullRowMapper
            = (rs, rowNum) -> {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setCategory_number(rs.getInt("category_number"));
        dto.setCategory_name(rs.getString("category_name"));
        dto.setTotal_sold(rs.getInt("total_sold"));
        return dto;
    };

    private final RowMapper<CategoryResponseDto> simpleRowMapper
            = (rs, rowNum) -> {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setCategory_number(rs.getInt("category_number"));
        dto.setCategory_name(rs.getString("category_name"));
        return dto;
    };

    public List<CategoryResponseDto> findPopCategories() {
        return jdbcTemplate.query(
                        """
                        SELECT c.category_number, c.category_name,
                        SUM(s.product_number) AS total_sold
                        FROM category c
                        INNER JOIN product p ON c.category_number = p.category_number
                        INNER JOIN store_product sp ON p.id_product = sp.id_product
                        INNER JOIN sale s ON sp.upc = s.upc
                        GROUP BY c.category_number, c.category_name
                        ORDER BY SUM(s.product_number) DESC
                        LIMIT 2
                        """,
                        fullRowMapper
                ).stream()
                .toList();
    }

    public List<CategoryResponseDto> findAll() {
        return jdbcTemplate.query(
                        """
                        SELECT category_number, category_name
                        FROM category
                        ORDER BY category_name
                        """,
                        simpleRowMapper
                ).stream()
                .toList();
    }
}
