package org.example.repository.check;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CheckResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CheckRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CheckResponseDto> mapper
            = (rs, rowNum) -> {
        CheckResponseDto dto = new CheckResponseDto();
        dto.setCheck_number(rs.getString("check_number"));
        dto.setId_employee(rs.getString("id_employee"));
        dto.setCard_number(rs.getString("card_number"));
        dto.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
        dto.setSum_total(rs.getBigDecimal("sum_total"));
        dto.setVat(rs.getBigDecimal("vat"));
        return dto;
    };

    public List<CheckResponseDto> findAll() {
        return jdbcTemplate.query(
                        """
                        SELECT check_number, id_employee, card_number, print_date, sum_total, vat
                        FROM checks
                        ORDER BY check_number
                        """,
                        mapper
                ).stream()
                .toList();
    }
}
