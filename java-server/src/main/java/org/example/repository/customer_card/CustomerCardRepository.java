package org.example.repository.customer_card;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CustomerCardResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomerCardRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CustomerCardResponseDto> mapper
            = (rs, rowNum) -> {
        CustomerCardResponseDto dto = new CustomerCardResponseDto();
        dto.setCard_number(rs.getString("card_number"));
        dto.setCust_surname(rs.getString("cust_surname"));
        dto.setCust_name(rs.getString("cust_name"));
        dto.setCust_patronymic(rs.getString("cust_patronymic"));
        dto.setPhone_number(rs.getString("phone_number"));
        dto.setCity(rs.getString("city"));
        dto.setStreet(rs.getString("street"));
        dto.setZip_code(rs.getString("zip_code"));
        dto.setPercent(rs.getInt("percent"));
        return dto;
    };

    public List<CustomerCardResponseDto> findAll() {
        return jdbcTemplate.query(
                        """
                        SELECT card_number, customer_surname AS cust_surname,
                               customer_name AS cust_name,
                               customer_patronymic AS cust_patronymic,
                               phone_number, city, street, zip_code,
                               customer_percent AS percent
                        FROM customer_card
                        ORDER BY card_number
                        """,
                        mapper
                ).stream()
                .toList();
    }
}
