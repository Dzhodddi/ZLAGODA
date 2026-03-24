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

    public PageResponseDto<ProductDto> findSold(Pageable pageable) {
        long offset = pageable.getOffset();
        List<ProductDto> products = jdbcTemplate.query(
                """
                SELECT p.id_product, p.product_name,
                       SUM(s1.product_number) AS sold_number,
                       SUM(s1.selling_price * s1.product_number) AS total_sold
                FROM product p
                INNER JOIN store_product sp1
                    ON sp1.id_product = p.id_product
                INNER JOIN sale s1
                    ON s1.UPC = sp1.UPC
                WHERE NOT EXISTS(
                        SELECT 1
                        FROM store_product sp3
                        WHERE sp3.id_product = p.id_product
                        AND NOT EXISTS(
                            SELECT 1
                            FROM sale s2
                            WHERE s2.UPC = sp3.UPC
                        )
                      )
                GROUP BY p.id_product, p.product_name
                ORDER BY p.id_product
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                rowMapper,
                offset,
                pageable.getPageSize()
        ).stream()
                .map(productMapper::toDto)
                .toList();

        long total = getSoldCount();
        return PageResponseDto.of(products, pageable.getPageSize(), total,
                offset + products.size() < total);
    }

    public PageResponseDto<ProductDto> findAll(Pageable pageable) {
        long offset = pageable.getOffset();
        List<ProductDto> products = jdbcTemplate.query(
                """
                SELECT p.id_product, p.product_name, p.producer, p.product_characteristics,
                       c.category_number, c.category_name
                FROM product p
                INNER JOIN category c
                ON p.category_number = c.category_number
                ORDER BY p.id_product
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                rowMapper,
                offset,
                pageable.getPageSize()
        ).stream()
                .map(productMapper::toDto)
                .toList();

        long total = getTotalCount();
        return PageResponseDto.of(products, pageable.getPageSize(), total,
                offset + products.size() < total);
    }

    public PageResponseDto<ProductDto> findAllSortedByName(Pageable pageable) {
        long offset = pageable.getOffset();
        List<ProductDto> products = jdbcTemplate.query(
                        """
                        SELECT p.id_product, p.product_name, p.producer, p.product_characteristics,
                               c.category_number, c.category_name
                        FROM product p
                        INNER JOIN category c
                        ON p.category_number = c.category_number
                        ORDER BY p.product_name
                        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                        """,
                        rowMapper,
                        offset,
                        pageable.getPageSize()
                ).stream()
                .map(productMapper::toDto)
                .toList();

        long total = getTotalCount();
        return PageResponseDto.of(products, pageable.getPageSize(), total,
                offset + products.size() < total);
    }

    public PageResponseDto<ProductDto> findByName(String name, Pageable pageable) {
        long offset = pageable.getOffset();
        List<ProductDto> products = jdbcTemplate.query(
                """
                SELECT p.id_product, p.product_name, p.producer, p.product_characteristics,
                       c.category_number, c.category_name
                FROM product p
                INNER JOIN category c
                ON p.category_number = c.category_number
                WHERE product_name ILIKE ?
                ORDER BY id_product
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                rowMapper,
                "%" + name + "%",
                offset,
                pageable.getPageSize()
        ).stream().map(productMapper::toDto).toList();

        long total = getNameCount(name);
        return PageResponseDto.of(products, pageable.getPageSize(), total,
                offset + products.size() < total);
    }

    public PageResponseDto<ProductDto> findByCategoryId(int categoryNumber,
                                                        Pageable pageable) {
        long offset = pageable.getOffset();
        List<ProductDto> products = jdbcTemplate.query(
                """
                SELECT p.id_product, p.product_name, p.producer, p.product_characteristics,
                       c.category_number, c.category_name
                FROM product p
                INNER JOIN category c
                ON p.category_number = c.category_number
                WHERE p.category_number = ?
                ORDER BY product_name
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """,
                rowMapper,
                categoryNumber,
                offset,
                pageable.getPageSize()
        )
                .stream()
                .map(productMapper::toDto)
                .toList();

        long total = getCategoryIdCount(categoryNumber);
        return PageResponseDto.of(products, pageable.getPageSize(), total,
                offset + products.size() < total);
    }

    public Optional<ProductDto> findById(int id) {
        try {
            Optional<Product> res = Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                            SELECT p.id_product,
                                   p.product_name,
                                   p.producer,
                                   p.product_characteristics,
                                   c.category_number,
                                   c.category_name
                            FROM product p
                            INNER JOIN category c
                            ON p.category_number = c.category_number
                            WHERE id_product = ?
                            """,
                            rowMapper,
                            id
                    )
            );
            return res.map(productMapper::toDto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ProductDto save(Product product) {
        try {
            if (product.getId_product() == 0) {
                Product res = jdbcTemplate.queryForObject(
                        """
                        INSERT INTO product (
                            product_name,
                            producer,
                            product_characteristics,
                            category_number
                        ) VALUES (?, ?, ?, ?)
                        RETURNING category_number, product_name, producer, product_characteristics
                        """,
                        rowMapper,
                        product.getProduct_name(),
                        product.getProducer(),
                        product.getProduct_characteristics(),
                        product.getCategory_number()
                );
                return productMapper.toDto(res);
            } else {
                int updated = jdbcTemplate.update(
                        """
                        UPDATE product SET
                            product_name = ?,
                            producer = ?,
                            product_characteristics = ?,
                            category_number = ?
                        WHERE id_product = ?
                        """,
                        product.getProduct_name(),
                        product.getProducer(),
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
                        producer = ?,
                        product_characteristics = ?,
                        category_number = ?
                    WHERE id_product = ?
                    """,
                    requestDto.getProduct_name(),
                    requestDto.getProducer(),
                    requestDto.getProduct_characteristics(),
                    requestDto.getCategory_number(),
                    id
            );

            if (updatedRows == 0) {
                throw new EntityNotFoundException("Update failed, product not found: " + id);
            }

            return findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found after update: " + id));

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
                            SELECT id_product, product_name, producer, product_characteristics
                            FROM product
                            ORDER BY id_product
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
                WHERE product_name ILIKE ?
                """,
                Integer.class,
                "%" + name + "%"
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

    private long getSoldCount() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(DISTINCT p.id_product)
                FROM product p
                WHERE EXISTS (
                        SELECT 1
                        FROM store_product sp
                        WHERE sp.id_product = p.id_product
                      ) AND NOT EXISTS(
                        SELECT 1
                        FROM store_product sp
                        WHERE sp.id_product = p.id_product
                        AND NOT EXISTS(
                            SELECT 1
                            FROM sale s
                            WHERE s.UPC = sp.UPC
                        )
                      )
                """,
                Integer.class
        );
        return count != null ? count : 0;
    }
}
