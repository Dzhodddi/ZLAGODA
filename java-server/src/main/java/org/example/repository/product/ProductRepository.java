package org.example.repository.product;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.mapper.product.ProductRowMapper;
import org.example.model.product.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRowMapper rowMapper;

    public List<Product> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM product",
                rowMapper
        );
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM product WHERE id_product = ?",
                        rowMapper,
                        id
                )
        );
    }

    public Product save(Product product) {
        if (product.getIdProduct() == null) {
            return jdbcTemplate.queryForObject(
                    """
                    INSERT INTO product (product_name, product_characteristics, category_number)
                    VALUES (?, ?, ?)
                    RETURNING *
                    """,
                    rowMapper,
                    product.getProductName(),
                    product.getProductCharacteristics(),
                    product.getCategoryNumber()
            );
        }

        return jdbcTemplate.queryForObject(
                """
                UPDATE product SET
                    product_name = ?,
                    product_characteristics = ?,
                    category_number = ?
                WHERE id_product = ?
                RETURNING *
                """,
                rowMapper,
                product.getProductName(),
                product.getProductCharacteristics(),
                product.getCategoryNumber(),
                product.getIdProduct()
        );
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(
                "DELETE FROM product WHERE id_product = ?",
                id
        );
    }
}
