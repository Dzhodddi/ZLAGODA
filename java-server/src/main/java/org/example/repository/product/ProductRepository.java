package org.example.repository.product;

import lombok.RequiredArgsConstructor;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidCategoryException;
import org.example.mapper.product.ProductMapper;
import org.example.mapper.product.ProductRowMapper;
import org.example.model.product.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper rowMapper;
    private final ProductMapper productMapper;

    public Page<ProductDto> findAll(Pageable pageable) {
        List<ProductDto> products = jdbcTemplate.query(
                """
                    SELECT *
                    FROM product 
                    ORDER BY product_name 
                    LIMIT ?
                    OFFSET ?
                    """,
                rowMapper,
                        pageable.getPageSize(),
                        pageable.getOffset()
                ).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        long total = getTotalCount();
        return new PageImpl<>(products, pageable, total);
    }

    public Optional<Product> findById(int id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT *
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

    public Page<ProductDto> findByName(String name, Pageable pageable) {
        List<ProductDto> products = jdbcTemplate.query(
                            """
                            SELECT *
                            FROM product
                            WHERE product_name = ?
                            LIMIT ?
                            OFFSET ?
                            """,
                            rowMapper,
                            name,
                        pageable.getPageSize(),
                        pageable.getOffset())
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM product
                    WHERE product_name = ?
                    """,
                Integer.class,
                name
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(products, pageable, total);
    }

    public Page<ProductDto> findByCategoryId(int category_number, Pageable pageable) {
        List<ProductDto> products = jdbcTemplate.query(
                """
                     SELECT *
                     FROM product
                     WHERE category_number = ?
                     ORDER BY product_name
                     LIMIT ?
                     OFFSET ?
                     """,
                rowMapper,
                category_number,
                pageable.getPageSize(),
                pageable.getOffset())
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        Integer count = jdbcTemplate.queryForObject(
                """
                    SELECT COUNT(*)
                    FROM product
                    WHERE category_number = ?
                    """,
                Integer.class,
                category_number
        );
        long total = count != null ? count : 0;
        return new PageImpl<>(products, pageable, total);
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
                        RETURNING *
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
                            SELECT *
                            FROM product
                            ORDER BY product_name
                            """,
                        rowMapper)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    private long getTotalCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM product""",
                Integer.class
        );
        return count != null ? count : 0;
    }
}
