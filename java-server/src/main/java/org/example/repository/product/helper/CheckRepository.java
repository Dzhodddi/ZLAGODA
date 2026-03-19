package org.example.repository.product.helper;

import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CategoryResponseDto;
import org.example.dto.helper.CheckResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CheckRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CheckResponseDto> mapper
            = (rs, rowNum) -> {
        CheckResponseDto dto = new CheckResponseDto();
        dto.setCheck_number(rs.getString("check_number"));
        return dto;
    };

    public List<CheckResponseDto> findAll() {
        return jdbcTemplate.query(
                        """
                        SELECT check_number
                        FROM checks
                        ORDER BY check_number
                        """,
                        mapper
                ).stream()
                .toList();
    }
}
