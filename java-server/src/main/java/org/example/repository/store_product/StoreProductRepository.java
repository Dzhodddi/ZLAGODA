package org.example.repository.store_product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.store_product.product.*;
import org.example.exception.custom_exception.EntityHasRelationsException;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidProductException;
import org.example.mapper.store_product.StoreProductMapper;
import org.example.mapper.store_product.StoreProductRowMapper;
import org.example.model.store_product.StoreProduct;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class StoreProductRepository {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.20");
    private static final BigDecimal PROM_RATE = new BigDecimal("0.80");
    private final JdbcTemplate jdbcTemplate;
    private final StoreProductRowMapper rowMapper;
    private final StoreProductMapper storeProductMapper;

    private final RowMapper<StoreProductWithNameDto> withNameRowMapper
            = (rs, rowNum) -> {
        StoreProductWithNameDto dto = new StoreProductWithNameDto();
        dto.setUPC(rs.getString("UPC"));
        dto.setUPC_prom(rs.getString("UPC_prom"));
        dto.setId_product(rs.getInt("id_product"));
        dto.setSelling_price(rs.getBigDecimal("selling_price"));
        dto.setProducts_number(rs.getInt("products_number"));
        dto.setPromotional_product(rs.getBoolean("promotional_product"));
        dto.setProduct_name(rs.getString("product_name"));
        return dto;
    };

    public Optional<StoreProductWithNameDto> findByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                                   sp.products_number, sp.promotional_product, p.product_name
                            FROM store_product sp
                            INNER JOIN product p
                                ON sp.id_product = p.id_product
                            WHERE upc = ?
                            """,
                            withNameRowMapper,
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
                            WHERE UPC = ?
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

    public Optional<StoreProductCharacteristicsDto> findProductInfoByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT sp.selling_price, sp.products_number, p.product_name, p.product_characteristics
                            FROM store_product sp
                            INNER JOIN product p
                            ON sp.id_product = p.id_product
                            WHERE sp.UPC = ?
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

    public PageResponseDto<StoreProductWithNameDto> findAll(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                ORDER BY sp.UPC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper,
                offset,
                pageable.getPageSize()
        );
        long total = getTotalCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findAllSortedByName(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                ORDER BY p.product_name
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper,
                offset,
                pageable.getPageSize()
        );
        long total = getTotalCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findAllSortedByQuantity(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                ORDER BY sp.products_number
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getTotalCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findPromotional(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = true
                ORDER BY sp.UPC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findNonPromotional(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = false
                ORDER BY sp.UPC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getNonPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findPromotionalSortedByQuantity(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = true
                ORDER BY sp.products_number, sp.UPC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findNonPromotionalSortedByQuantity(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = false
                ORDER BY sp.products_number, sp.UPC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getNonPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findPromotionalSortedByName(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = true
                ORDER BY p.product_name
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    public PageResponseDto<StoreProductWithNameDto> findNonPromotionalSortedByName(Pageable pageable) {
        long offset = pageable.getOffset();
        List<StoreProductWithNameDto> items = jdbcTemplate.query(
                """
                SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p ON sp.id_product = p.id_product
                WHERE sp.promotional_product = false
                ORDER BY p.product_name
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                withNameRowMapper, offset, pageable.getPageSize()
        );
        long total = getNonPromCount();
        return PageResponseDto.of(items, pageable.getPageSize(), total,
                offset + items.size() < total);
    }

    @Transactional
    public StoreProduct save(StoreProductRequestDto requestDto) {
        BigDecimal priceWithVat = (requestDto.getSelling_price())
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal priceWithVatAndDiscount = (requestDto.getSelling_price().multiply(PROM_RATE))
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);
        try {
        if (requestDto.isPromotional_product()) {
            validateDb(requestDto.getId_product(), true);
            jdbcTemplate.update(
                    """
                    INSERT INTO store_product (
                        UPC,
                        UPC_prom,
                        id_product,
                        selling_price,
                        products_number,
                        promotional_product
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    requestDto.getUPC(),
                    null,
                    requestDto.getId_product(),
                    priceWithVatAndDiscount,
                    requestDto.getProducts_number(),
                    true
            );
        } else {
            if (requestDto.getUPC_prom() != null) {
                validateDb(requestDto.getId_product(), true);
                jdbcTemplate.update(
                        """
                        INSERT INTO store_product (
                            UPC,
                            UPC_prom,
                            id_product,
                            selling_price,
                            products_number,
                            promotional_product
                        ) VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        requestDto.getUPC_prom(),
                        null,
                        requestDto.getId_product(),
                        priceWithVatAndDiscount,
                        requestDto.getProducts_number(),
                        true
                );
            }
            validateDb(requestDto.getId_product(), false);
            jdbcTemplate.update(
                    """
                    INSERT INTO store_product (
                        UPC,
                        UPC_prom,
                        id_product,
                        selling_price,
                        products_number,
                        promotional_product
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    requestDto.getUPC(),
                    requestDto.getUPC_prom(),
                    requestDto.getId_product(),
                    priceWithVat,
                    requestDto.getProducts_number(),
                    false
            );
        }
        } catch (DataIntegrityViolationException | UncategorizedSQLException e) {
            throw new InvalidProductException(
                    "Invalid product or UPC reference: " + requestDto.getId_product());
        }
       return findAllInfoByUPC(requestDto.getUPC()).orElseThrow(() ->
               new EntityNotFoundException("Not found store product after creating: " + requestDto.getUPC()));
    }

    @Transactional
    public StoreProductDto updateByUPC(String upc, StoreProductRequestDto requestDto) {
        if (!existsByUPC(upc)) {
            throw new EntityNotFoundException("Store product not found: " + upc);
        }
        if (requestDto.isPromotional_product() && requestDto.getUPC_prom() != null) {
            throw new InvalidProductException("Promotional product cannot have UPC_prom");
        }

        BigDecimal priceWithVat = (requestDto.getSelling_price())
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal priceWithVatAndDiscount = (requestDto.getSelling_price()
                .multiply(PROM_RATE))
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        if (requestDto.isPromotional_product()) {
            Optional<StoreProduct> existingProm = findPromById_Product(requestDto.getId_product());
            if (existingProm.isPresent() && !existingProm.get().getUPC().equals(upc)) {
                throw new InvalidProductException(
                        "Promotional record already exists for product: " + requestDto.getId_product());
            }
        } else {
            Optional<StoreProduct> existingProm = findNonPromById_Product(requestDto.getId_product());
            if (existingProm.isPresent() && !existingProm.get().getUPC().equals(upc)) {
                throw new InvalidProductException(
                        "Non-promotional record already exists for product: " + requestDto.getId_product());
            }
        }

        if (!requestDto.isPromotional_product() && requestDto.getUPC_prom() != null) {
            try {
                if (!existsByUPC(requestDto.getUPC_prom())) {
                    validateDb(requestDto.getId_product(), true);
                    jdbcTemplate.update(
                            """
                            INSERT INTO store_product (
                                UPC,
                                UPC_prom,
                                id_product,
                                selling_price,
                                products_number,
                                promotional_product
                            ) VALUES (?, ?, ?, ?, ?, ?)
                            """,
                            requestDto.getUPC_prom(),
                            null,
                            requestDto.getId_product(),
                            priceWithVatAndDiscount,
                            requestDto.getProducts_number(),
                            true
                    );
                } else {
                    // not to remove the connection between prom and non-prom store product
                    jdbcTemplate.update(
                            """
                                UPDATE store_product
                                SET id_product = ?, selling_price = ?
                                WHERE UPC = ?
                                """,
                            requestDto.getId_product(),
                            priceWithVatAndDiscount,
                            requestDto.getUPC_prom());
                }

                jdbcTemplate.update(
                        """
                        UPDATE store_product
                        SET UPC_prom = ?,
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
                        false,
                        upc
                );

            } catch (DataIntegrityViolationException | UncategorizedSQLException e) {
                throw new InvalidProductException(
                        "Invalid product or UPC reference: " + requestDto.getId_product());
            }
        } else if (requestDto.isPromotional_product() && requestDto.getUPC_prom() == null) {
            try {

                jdbcTemplate.update(
                        """
                        UPDATE store_product
                        SET id_product = ?,
                            selling_price = ?,
                            products_number = ?,
                            promotional_product = ?,
                            UPC_prom = NULL
                        WHERE UPC = ?
                        """,
                        requestDto.getId_product(),
                        priceWithVatAndDiscount,
                        requestDto.getProducts_number(),
                        true,
                        upc
                );

                findNonPromById_Product(requestDto.getId_product()).ifPresent(nonProm -> {
                    if (!nonProm.getUPC().equals(upc)) {
                        jdbcTemplate.update(
                                """
                                UPDATE store_product
                                SET selling_price = ?, UPC_prom = ?
                                WHERE UPC = ?
                                """,
                                priceWithVat,
                                requestDto.getUPC(),
                                nonProm.getUPC()
                        );
                    }
                });

            } catch (DataIntegrityViolationException | UncategorizedSQLException e) {
                throw new InvalidProductException(
                        "Invalid product or UPC reference: " + requestDto.getId_product());
            }
        } else {
            try {
                jdbcTemplate.update(
                        """
                        UPDATE store_product
                        SET UPC_prom = NULL,
                            id_product = ?,
                            selling_price = ?,
                            products_number = ?,
                            promotional_product = false
                        WHERE UPC = ?
                        """,
                        requestDto.getId_product(),
                        priceWithVat,
                        requestDto.getProducts_number(),
                        upc
                );
            } catch (DataIntegrityViolationException | UncategorizedSQLException e) {
                throw new InvalidProductException(
                        "Invalid product or UPC reference: " + requestDto.getId_product());
            }
        }

        return findAllInfoByUPC(upc)
                .map(storeProductMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Store product not found after update: " + upc));
    }

    public void deleteByUPC(String upc) {
        if (!existsByUPC(upc)) {
            throw new EntityNotFoundException("Store product not found: " + upc);
        }
        try {
            jdbcTemplate.update("""
                                DELETE
                                FROM store_product
                                WHERE UPC = ?
                                """,
                    upc);
        } catch (DataIntegrityViolationException e) {
            throw new EntityHasRelationsException("Store product is used: " + upc);
        }
    }

    public boolean existsByUPC(String upc) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                WHERE UPC = ?
                """,
                Integer.class,
                upc
        );
        return count != null && count > 0;
    }

    public void updateProductPrice(String upc, BigDecimal price) {
        jdbcTemplate.update(
                """
                UPDATE store_product
                SET selling_price = ?
                WHERE UPC = ?
                """,
                price, upc
        );
    }

    public PageResponseDto<StoreProductDto> findAll(
            Pageable pageable, String lastSeenUPC) {

        List<StoreProductDto> items;

        if (lastSeenUPC != null) {
            items = jdbcTemplate.query(
                    """
                    SELECT UPC, UPC_prom, id_product, selling_price,
                           products_number, promotional_product
                    FROM store_product
                    WHERE UPC > ?
                    ORDER BY UPC
                    FETCH FIRST ? ROWS ONLY
                    """,
                    rowMapper,
                    lastSeenUPC,
                    pageable.getPageSize()
            ).stream().map(storeProductMapper::toDto).toList();
        } else {
            items = jdbcTemplate.query(
                    """
                    SELECT UPC, UPC_prom, id_product, selling_price,
                           products_number, promotional_product
                    FROM store_product
                    ORDER BY UPC
                    FETCH FIRST ? ROWS ONLY
                    """,
                    rowMapper,
                    pageable.getPageSize()
            ).stream().map(storeProductMapper::toDto).toList();
        }

        long total = getTotalCount();
        boolean hasNext = items.size() == pageable.getPageSize();
        return PageResponseDto.of(items, pageable.getPageSize(), total, hasNext);
    }

    public Optional<StoreProduct> findAllInfoByUPC(String upc) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT UPC, UPC_prom, id_product, selling_price,
                                   products_number, promotional_product
                            FROM store_product
                            WHERE UPC = ?
                            """,
                            rowMapper,
                            upc
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<StoreProductDto> findAllNoPagination() {
        return jdbcTemplate.query("""
             SELECT UPC, UPC_prom, id_product, selling_price,
                    products_number, promotional_product
             FROM store_product
             ORDER BY UPC
             """,
                        rowMapper)
                .stream()
                .map(storeProductMapper::toDto)
                .toList();
    }

    public List<StoreProductWithNameDto> findAllWithNameNoPagination() {
        return jdbcTemplate.query(
             """
             SELECT sp.UPC, sp.UPC_prom, sp.id_product, sp.selling_price,
                       sp.products_number, sp.promotional_product, p.product_name
                FROM store_product sp
                INNER JOIN product p
                ON sp.id_product = p.id_product
                ORDER BY sp.promotional_product DESC
             """,
                        withNameRowMapper)
                .stream()
                .toList();
    }

    public Optional<StoreProduct> findPromById_Product(int id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT UPC, UPC_prom, id_product, selling_price,
                                   products_number, promotional_product
                            FROM store_product
                            WHERE id_product = ? AND promotional_product = true
                            """,
                            rowMapper,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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

    private long getPromCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = true
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }

    private long getNonPromCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                WHERE promotional_product = false
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }

    private Optional<StoreProduct> findNonPromById_Product(int id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT UPC, UPC_prom, id_product, selling_price,
                                   products_number, promotional_product
                            FROM store_product
                            WHERE id_product = ? AND promotional_product = false
                            """,
                            rowMapper,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void validateDb(int id_product, boolean isPromotional) {
       if (!promotionalCheck(id_product, isPromotional)) {
           throw new InvalidProductException("Can't insert store product as the"
                   + " database already has such kind of store product for this type of product: "
                   + id_product);
       }
    }

    private boolean promotionalCheck(int id_product,
                                     boolean isPromotional) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM store_product
                WHERE id_product = ? AND promotional_product = ?
                """,
                Integer.class,
                id_product,
                isPromotional
        );
        return count != null && count < 1;
    }
}
