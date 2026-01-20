package org.example.repository.store_product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.StoreProductDto;
import org.example.dto.store_product.StoreProductRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidProductException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.mapper.store_product.StoreProductRowMapper;
import org.example.model.store_product.StoreProduct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StoreProductRepository {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.20");
    private static final BigDecimal PROM_RATE = new BigDecimal("0.80");
    private final JdbcTemplate jdbcTemplate;
    private final StoreProductRowMapper rowMapper;
    @Qualifier("storeProductMapper")
    private final StoreProductMapper mapper;

    public List<StoreProduct> findAll() {
        return jdbcTemplate.query("SELECT * FROM store_product", rowMapper);
    }

    public Optional<StoreProduct> findByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM store_product WHERE UPC = ?",
                            rowMapper,
                            upc
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public StoreProduct save(StoreProductRequestDto requestDto) {
        BigDecimal priceWithVat;
        if (requestDto.isPromotional_product()) {
            priceWithVat = (requestDto.getPrice().multiply(PROM_RATE))
                    .multiply(BigDecimal.ONE.add(VAT_RATE))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            priceWithVat = (requestDto.getPrice())
                    .multiply(BigDecimal.ONE.add(VAT_RATE))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        try {
            return jdbcTemplate.queryForObject(
                    """
                    INSERT INTO store_product (
                        UPC,
                        UPC_prom,
                        id_product,
                        selling_price,
                        products_number,
                        promotional_product
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING *
                    """,
                    rowMapper,
                    requestDto.getUPC(),
                    requestDto.getUPC_prom(),
                    requestDto.getId_product(),
                    priceWithVat,
                    requestDto.getProducts_number(),
                    requestDto.isPromotional_product()
            );
        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductException(
                    "Invalid product or UPC reference: " + requestDto.getId_product());
        }
    }

    public StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto) {
        if (!existsByUPC(upc)) {
            throw new EntityNotFoundException("Store product not found: " + upc);
        }
        BigDecimal priceWithVat;
        if (requestDto.isPromotional_product()) {
            priceWithVat = (requestDto.getPrice().multiply(PROM_RATE))
                    .multiply(BigDecimal.ONE.add(VAT_RATE))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            priceWithVat = (requestDto.getPrice())
                    .multiply(BigDecimal.ONE.add(VAT_RATE))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        try {
            int updatedRows = jdbcTemplate.update(
                    """
                    UPDATE store_product SET
                        UPC_prom = ?,
                        id_product = ?,
                        selling_price = ?,
                        products_number = ?,
                        promotional_product = ?
                    WHERE UPC = ?
                    """,
                    requestDto.getUPC_prom(),
                    requestDto.getId_product(),
                    priceWithVat,
                    requestDto.getProducts_number(),
                    requestDto.isPromotional_product(),
                    upc
            );

            if (updatedRows == 0) {
                throw new EntityNotFoundException("Update failed, store product not found: " + upc);
            }

            return findByUPC(upc)
                    .map(mapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Store product not found after update: " + upc));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductException("Invalid product or UPC reference: " + requestDto.getId_product());
        }
    }

    public void deleteByUPC(String upc) {
        if (!existsByUPC(upc)) {
            throw new EntityNotFoundException("Store product not found: " + upc);
        }
        jdbcTemplate.update("DELETE FROM store_product WHERE UPC = ?", upc);
    }

    public boolean existsByUPC(String upc) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM store_product WHERE UPC = ?",
                Integer.class,
                upc
        );
        return count != null && count > 0;
    }

    public void updateProductPriceAndPromotion(String upc,
                                               BigDecimal price,
                                               boolean promotional) {
        jdbcTemplate.update(
                """
                UPDATE store_product
                SET selling_price = ?, promotional_product = ?
                WHERE UPC = ?
                """,
                price, promotional, upc
        );
    }
}
