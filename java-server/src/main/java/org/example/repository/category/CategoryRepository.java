package org.example.repository.category;

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
                        WITH ranked AS (SELECT c.category_number, c.category_name,
                                                SUM(s.product_number) AS total_sold,
                                                DENSE_RANK() OVER (ORDER BY SUM(s.product_number) DESC) AS rnk
                                        FROM category c
                                        INNER JOIN product p ON c.category_number = p.category_number
                                        INNER JOIN store_product sp ON p.id_product = sp.id_product
                                        INNER JOIN sale s ON sp.upc = s.upc
                                        GROUP BY c.category_number, c.category_name
                                        ),
                            leader_count AS (SELECT COUNT(*) AS cnt
                                            FROM ranked
                                            WHERE rnk = 1
                                            )
                        SELECT r.category_number, r.category_name, r.total_sold
                        FROM ranked r, leader_count lc
                        WHERE (r.rnk = 1 OR (lc.cnt = 1 AND r.rnk = 2)) AND r.total_sold > 0
                        ORDER BY r.total_sold DESC, r.category_number ASC;
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
                        ORDER BY category_number
                        """,
                        simpleRowMapper
                ).stream()
                .toList();
    }
}
