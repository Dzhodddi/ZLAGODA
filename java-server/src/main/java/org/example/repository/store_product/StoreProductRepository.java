package org.example.repository.store_product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.product.StoreProductCharacteristicsDto;
import org.example.dto.store_product.product.StoreProductDto;
import org.example.dto.store_product.product.StoreProductPriceAndQuantityDto;
import org.example.dto.store_product.product.StoreProductRequestDto;
import org.example.dto.store_product.product.StoreProductWithNameDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidProductException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.mapper.store_product.StoreProductRowMapper;
import org.example.model.store_product.StoreProduct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<StoreProductWithNameDto> findAllSortedByName(Pageable pageable) {
        List<StoreProductWithNameDto> storeProducts
                = new ArrayList<>(jdbcTemplate.query(
                        """
                        SELECT sp.*, p.product_name
                        FROM store_product sp
                        JOIN product p ON sp.id_product = p.id_product
                        WHERE sp.is_deleted = false
                        ORDER BY p.product_name
                        LIMIT ?
                        OFFSET ?
                        """,
                (rs, rowNum) -> {
                    StoreProductWithNameDto dto = new StoreProductWithNameDto();
                    dto.setUPC(rs.getString("UPC"));
                    dto.setUPC_prom(rs.getString("UPC_prom"));
                    dto.setId_product(rs.getInt("id_product"));
                    dto.setSelling_price(rs.getBigDecimal("selling_price"));
                    dto.setProducts_number(rs.getInt("products_number"));
                    dto.setPromotional_product(rs.getBoolean("promotional_product"));
                    dto.setProduct_name(rs.getString("product_name"));
                    return dto;
                },
                pageable.getPageSize(),
                pageable.getOffset()));
        long total = getTotalCount();
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Page<StoreProductDto> findAllSortedByQuantity(Pageable pageable) {
        List<StoreProductDto> storeProducts = jdbcTemplate.query(
                        """
                            SELECT *
                            FROM store_product
                            WHERE is_deleted = false
                            ORDER BY products_number
                            LIMIT ?
                            OFFSET ?
                            """,
                        rowMapper,
                        pageable.getPageSize(),
                        pageable.getOffset()
                ).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        long total = getTotalCount();
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Page<StoreProductDto> findPromotionalSortedByQuantity(Pageable pageable) {
        List<StoreProductDto> storeProducts = jdbcTemplate.query("""
             SELECT *
             FROM store_product
             WHERE promotional_product = true AND is_deleted = false
             ORDER BY products_number ASC
             LIMIT ?
             OFFSET ?
             """,
                        rowMapper,
                        pageable.getPageSize(),
                        pageable.getOffset()
                ).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM store_product
                    WHERE promotional_product = true AND is_deleted = false
                    """,
                Integer.class
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Page<StoreProductWithNameDto> findPromotionalSortedByName(Pageable pageable) {
        List<StoreProductWithNameDto> storeProducts
                = new ArrayList<>(jdbcTemplate.query(
                                 """
                                 SELECT sp.*, p.product_name
                                 FROM store_product sp
                                 INNER JOIN product p ON sp.id_product = p.id_product
                                 WHERE sp.promotional_product = true AND sp.is_deleted = false
                                 ORDER BY p.product_name ASC
                                 LIMIT ?
                                 OFFSET ?
                                 """,
                (rs, rowNum) -> {
                    StoreProductWithNameDto dto = new StoreProductWithNameDto();
                    dto.setUPC(rs.getString("UPC"));
                    dto.setUPC_prom(rs.getString("UPC_prom"));
                    dto.setId_product(rs.getInt("id_product"));
                    dto.setSelling_price(rs.getBigDecimal("selling_price"));
                    dto.setProducts_number(rs.getInt("products_number"));
                    dto.setPromotional_product(rs.getBoolean("promotional_product"));
                    dto.setProduct_name(rs.getString("product_name"));
                    return dto;
                },
                pageable.getPageSize(),
                pageable.getOffset()));
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM store_product
                    WHERE promotional_product = true AND is_deleted = false
                    """,
                Integer.class
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Page<StoreProductDto> findNonPromotionalSortedByQuantity(Pageable pageable) {
        List<StoreProductDto> storeProducts = jdbcTemplate.query(
                    """
                     SELECT * FROM store_product
                     WHERE promotional_product = false AND is_deleted = false
                     ORDER BY products_number ASC
                     LIMIT ?
                     OFFSET ?
                     """,
                        rowMapper,
                        pageable.getPageSize(),
                        pageable.getOffset()
                ).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM store_product
                    WHERE promotional_product = false AND is_deleted = false
                    """,
                Integer.class
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Page<StoreProductWithNameDto> findNonPromotionalSortedByName(Pageable pageable) {
        List<StoreProductWithNameDto> storeProducts
                = new ArrayList<>(jdbcTemplate.query("""
             SELECT sp.*, p.product_name
             FROM store_product sp
             INNER JOIN product p ON sp.id_product = p.id_product
             WHERE sp.promotional_product = false AND sp.is_deleted = false
             ORDER BY p.product_name ASC
             LIMIT ?
             OFFSET ?
             """,
                (rs, rowNum) -> {
                    StoreProductWithNameDto dto = new StoreProductWithNameDto();
                    dto.setUPC(rs.getString("UPC"));
                    dto.setUPC_prom(rs.getString("UPC_prom"));
                    dto.setId_product(rs.getInt("id_product"));
                    dto.setSelling_price(rs.getBigDecimal("selling_price"));
                    dto.setProducts_number(rs.getInt("products_number"));
                    dto.setPromotional_product(rs.getBoolean("promotional_product"));
                    dto.setProduct_name(rs.getString("product_name"));
                    return dto;
                },
                pageable.getPageSize(),
                pageable.getOffset()));
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM store_product
                    WHERE promotional_product = false AND is_deleted = false
                    """,
                Integer.class
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public Optional<StoreProduct> findAllInfoByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT *
                            FROM store_product
                            WHERE UPC = ? AND is_deleted = false
                            """,
                            rowMapper,
                            upc
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<StoreProductCharacteristicsDto> findByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT selling_price, products_number, product_name, p.product_characteristics
                            FROM store_product
                            INNER JOIN product p ON store_product.id_product = p.id_product
                            WHERE UPC = ? AND is_deleted = false
                            """,
                            (rs, rowNum) -> {
                                StoreProductCharacteristicsDto dto = new StoreProductCharacteristicsDto();
                                dto.setSelling_price(rs.getBigDecimal("selling_price"));
                                dto.setProducts_number(rs.getInt("products_number"));
                                dto.setProduct_name(rs.getString("product_name"));
                                dto.setProduct_characteristics(rs.getString("product_characteristics"));
                                return dto;
                            },
                            upc
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<StoreProductPriceAndQuantityDto> findPriceAndQuantityByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT selling_price, products_number
                            FROM store_product
                            WHERE UPC = ? AND is_deleted = false
                            """,
                            (rs, rowNum) -> {
                                StoreProductPriceAndQuantityDto dto = new StoreProductPriceAndQuantityDto();
                                dto.setSelling_price(rs.getBigDecimal("selling_price"));
                                dto.setProducts_number(rs.getInt("products_number"));
                                return dto;
                            },
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
                        promotional_product,
                        is_deleted
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    RETURNING *
                    """,
                    rowMapper,
                    requestDto.getUPC(),
                    requestDto.getUPC_prom(),
                    requestDto.getId_product(),
                    priceWithVat,
                    requestDto.getProducts_number(),
                    requestDto.isPromotional_product(),
                    Boolean.FALSE
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
                    UPDATE store_product
                    SET UPC_prom = ?,
                        id_product = ?,
                        selling_price = ?,
                        products_number = ?,
                        promotional_product = ?
                    WHERE UPC = ? AND is_deleted = false
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

            return findAllInfoByUPC(upc)
                    .map(mapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Store product not found after update: " + upc));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductException("Invalid product or UPC reference: " + requestDto.getId_product());
        }
    }

    public void softDeleteByUPC(String upc) {
        jdbcTemplate.update("""
                                UPDATE store_product
                                SET is_deleted = true
                                WHERE UPC = ? AND is_deleted = false
                                """, upc);
    }

    public boolean existsByUPC(String upc) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                WHERE UPC = ? AND is_deleted = false
                """,
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
                WHERE UPC = ? AND is_deleted = false
                """,
                price, promotional, upc
        );
    }

    public Page<StoreProductDto> findAll(Pageable pageable) {
        List<StoreProductDto> storeProducts = jdbcTemplate.query(
                """
                     SELECT *
                     FROM store_product
                     WHERE is_deleted = false
                     LIMIT ?
                     OFFSET ?
                     """,
                        rowMapper,
                        pageable.getPageSize(),
                        pageable.getOffset()
                ).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        long total = getTotalCount();
        return new PageImpl<>(storeProducts, pageable, total);
    }

    public List<StoreProductDto> findAllNoPagination() {
        return jdbcTemplate.query("""
             SELECT *
             FROM store_product
             WHERE is_deleted = false
             """,
                rowMapper)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private long getTotalCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }
}
