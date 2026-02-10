package org.example.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.CategoryResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CategoryResponseDto> rowMapper
            = (rs, rowNum) -> {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setCategory_name(rs.getString("category_name"));
        return dto;
    };

    public List<CategoryResponseDto> findPopCategories() {
        return jdbcTemplate.query(
                            """
                            SELECT category_name 
                            FROM category c
                            INNER JOIN product p
                            ON c.category_number = p.category_number
                            INNER JOIN store_product sp
                            ON p.id_product = sp.id_product
                            INNER JOIN sale s
                            ON sp.upc = s.upc
                            GROUP BY c.category_number, c.category_name
                            ORDER BY SUM(s.product_number) DESC
                            LIMIT 2
                            """,
                            rowMapper
                    ).stream()
                    .toList();
    }
}
