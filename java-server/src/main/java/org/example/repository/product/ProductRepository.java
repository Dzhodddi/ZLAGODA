package org.example.repository.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.page.PageResponseDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidCategoryException;
import org.example.mapper.product.ProductMapper;
import org.example.mapper.product.ProductRowMapper;
import org.example.model.product.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper rowMapper;
    private final ProductMapper productMapper;

    public PageResponseDto<ProductDto> findDeleted(String checkNumber,
                                                       Pageable pageable,
                                                       Integer lastSeenId) {
        List<ProductDto> products;
        if (lastSeenId != null && lastSeenId > 0) {
            products = jdbcTemplate.query(
                            """
                            SELECT p.id_product, p.category_number, p.product_name, p.product_characteristics
                            FROM product p
                            INNER JOIN store_product sp1
                            ON p.id_product = sp1.id_product
                            INNER JOIN sale s
                            ON sp1.UPC = s.UPC
                            WHERE p.id_product > ?
                              AND s.check_number = ?
                              AND NOT EXISTS(
                                  SELECT 1
                                  FROM store_product sp2
                                  WHERE sp1.UPC = sp2.UPC AND is_deleted <> true
                              )
                            ORDER BY p.product_name
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            lastSeenId,
                            checkNumber,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            products = jdbcTemplate.query(
                            """
                            SELECT p.id_product, p.category_number, p.product_name, p.product_characteristics
                            FROM product p
                            INNER JOIN store_product sp1
                            ON p.id_product = sp1.id_product
                            INNER JOIN sale s
                            ON sp1.UPC = s.UPC
                            WHERE s.check_number = ?
                              AND NOT EXISTS(
                                  SELECT 1
                                  FROM store_product sp2
                                  WHERE sp1.UPC = sp2.UPC AND is_deleted <> true
                              )
                            ORDER BY p.product_name
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            checkNumber,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        long total = getDeletedCount(checkNumber);
        boolean hasNext = products.size() == pageable.getPageSize();

        return PageResponseDto.of(products, pageable.getPageSize(), total, hasNext);
    }

    public PageResponseDto<ProductDto> findAll(Pageable pageable, Integer lastSeenId) {
        List<ProductDto> products;

        if (lastSeenId != null && lastSeenId > 0) {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE id_product > ?
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            lastSeenId,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        long total = getTotalCount();
        boolean hasNext = products.size() == pageable.getPageSize();

        return PageResponseDto.of(products, pageable.getPageSize(), total, hasNext);
    }

    public PageResponseDto<ProductDto> findByName(
            String name, Pageable pageable, Integer lastSeenId) {

        List<ProductDto> products;

        if (lastSeenId != null && lastSeenId > 0) {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE product_name = ?
                              AND id_product > ?
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            name,
                            lastSeenId,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE product_name = ?
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            name,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        long total = getNameCount(name);
        boolean hasNext = products.size() == pageable.getPageSize();

        return PageResponseDto.of(products, pageable.getPageSize(), total, hasNext);
    }

    public PageResponseDto<ProductDto> findByCategoryId(
            int categoryNumber, Pageable pageable, Integer lastSeenId) {

        List<ProductDto> products;

        if (lastSeenId != null && lastSeenId > 0) {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE category_number = ?
                              AND id_product > ?
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            categoryNumber,
                            lastSeenId,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            products = jdbcTemplate.query(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE category_number = ?
                            ORDER BY id_product
                            FETCH FIRST ? ROWS ONLY
                            """,
                            rowMapper,
                            categoryNumber,
                            pageable.getPageSize()
                    ).stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        long total = getCategoryIdCount(categoryNumber);
        boolean hasNext = products.size() == pageable.getPageSize();

        return PageResponseDto.of(products, pageable.getPageSize(), total, hasNext);
    }

    public Optional<Product> findById(int id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            WHERE id_product = ?
                            """,
                            rowMapper,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Product save(Product product) {
        try {
            if (product.getId_product() == 0) {
                return jdbcTemplate.queryForObject(
                        """
                        INSERT INTO product (
                            product_name,
                            product_characteristics,
                            category_number
                        ) VALUES (?, ?, ?)
                        RETURNING id_product, category_number, product_name, product_characteristics
                        """,
                        rowMapper,
                        product.getProduct_name(),
                        product.getProduct_characteristics(),
                        product.getCategory_number()
                );
            } else {
                int updated = jdbcTemplate.update(
                        """
                        UPDATE product SET
                            product_name = ?,
                            product_characteristics = ?,
                            category_number = ?
                        WHERE id_product = ?
                        """,
                        product.getProduct_name(),
                        product.getProduct_characteristics(),
                        product.getCategory_number(),
                        product.getId_product()
                );

                if (updated == 0) {
                    throw new EntityNotFoundException("Product not found: " + product.getId_product());
                }

                return findById(product.getId_product())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Product not found after update: " + product.getId_product()));
            }
        } catch (DataIntegrityViolationException e) {
            throw new InvalidCategoryException("Invalid category: "
                    + product.getCategory_number());
        }
    }

    public ProductDto updateProductById(int id, ProductRequestDto requestDto) {
        if (!existsByIdProduct(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }

        try {
            int updatedRows = jdbcTemplate.update(
                    """
                    UPDATE product SET
                        product_name = ?,
                        product_characteristics = ?,
                        category_number = ?
                    WHERE id_product = ?
                    """,
                    requestDto.getProduct_name(),
                    requestDto.getProduct_characteristics(),
                    requestDto.getCategory_number(),
                    id
            );

            if (updatedRows == 0) {
                throw new EntityNotFoundException("Update failed, product not found: " + id);
            }

            return findById(id)
                    .map(productMapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found after update: " + id));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidCategoryException("Invalid category: " + requestDto.getCategory_number());
        }
    }

    public void deleteById(int id) {
        if (!existsByIdProduct(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        jdbcTemplate.update("""
                        DELETE
                        FROM product
                        WHERE id_product = ?
                        """, id);
    }

    public boolean existsByIdProduct(int idProduct) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM product
                WHERE id_product = ?
                """,
                Integer.class,
                idProduct
        );
        return count != null && count > 0;
    }

    public List<ProductDto> findAllNoPagination() {
        return jdbcTemplate.query("""
                            SELECT id_product, category_number, product_name, product_characteristics
                            FROM product
                            ORDER BY product_name
                            """,
                        rowMapper)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    private long getTotalCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM product
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }

    private long getNameCount(String name) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM product
                WHERE product_name = ?
                """,
                Integer.class,
                name
        );
        return count != null ? count : 0;
    }

    private long getCategoryIdCount(int category_number) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM product
                WHERE category_number = ?
                """,
                Integer.class,
                category_number
        );
        return count != null ? count : 0;
    }

    private long getDeletedCount(String checkNumber) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(DISTINCT p.id_product)
                FROM product p
                INNER JOIN store_product sp
                ON p.id_product = sp.id_product
                INNER JOIN sale s
                ON sp.UPC = s.UPC
                WHERE s.check_number = ?
                  AND sp.is_deleted = true
                """,
                Integer.class,
                checkNumber
        );
        return count != null ? count : 0;
    }
}
