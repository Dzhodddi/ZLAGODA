package org.example.repository.store_product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.batch.BatchRequestDto;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidProductException;
import org.example.model.store_product.StoreProduct;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class BatchRepository {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.20");
    private static final int MIN_QUANTITY_FOR_PROMOTION = 10;
    private static final int DAYS_BEFORE_EXPIRY = 3;
    private static final BigDecimal PROM_RATE = new BigDecimal("0.80");

    private final JdbcTemplate jdbcTemplate;
    private final StoreProductRepository storeProductRepository;

    @Transactional
    public StoreProduct save(BatchRequestDto requestDto) {
        BigDecimal priceWithVat = (requestDto.getPrice())
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal priceWithVatAndDiscount = (requestDto.getPrice()
                .multiply(PROM_RATE))
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        try {
            StoreProduct storeProduct = storeProductRepository.findAllInfoByUPC(requestDto.getUPC())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Store product not found: " + requestDto.getUPC()
                    ));

            int updatedQuantity = storeProduct.getProducts_number() + requestDto.getQuantity();
            boolean shouldBePromotional = checkPromotional(updatedQuantity, requestDto.getExpiring_date());

            jdbcTemplate.update(
                    """
                    INSERT INTO batch (
                        UPC,
                        delivery_date,
                        expiring_date,
                        quantity,
                        selling_price
                    ) VALUES (?, ?, ?, ?, ?)
                    """,
                    requestDto.getUPC(),
                    requestDto.getDelivery_date(),
                    requestDto.getExpiring_date(),
                    requestDto.getQuantity(),
                    priceWithVat
            );

            storeProductRepository.updateProductPrice(
                    storeProduct.getUPC(),
                    priceWithVat
            );

            int updatedQuantityProm = 0;
            if (storeProduct.getUPC_prom() != null && storeProductRepository
                    .findAllInfoByUPC(storeProduct.getUPC_prom()).isPresent()) {
                updatedQuantityProm = storeProductRepository
                        .findAllInfoByUPC(storeProduct.getUPC_prom()).get()
                        .getProducts_number() + requestDto.getQuantity();
                storeProductRepository.updateProductPrice(
                        storeProduct.getUPC_prom(),
                        priceWithVatAndDiscount
                );
            }

            if (!shouldBePromotional) {
                jdbcTemplate.update(
                        """
                        UPDATE store_product
                        SET products_number = ?
                        WHERE UPC = ?
                        """,
                        updatedQuantity,
                        storeProduct.getUPC()
                );
            }

            if (shouldBePromotional && storeProduct.getUPC_prom() != null) {
                jdbcTemplate.update(
                        """
                        UPDATE store_product
                        SET products_number = ?
                        WHERE UPC = ?
                        """,
                        updatedQuantityProm,
                        storeProduct.getUPC_prom()
                );
            }

            if (shouldBePromotional && storeProduct.getUPC_prom() == null) {
                Optional<StoreProduct> existingProm = storeProductRepository
                        .findPromById_Product(storeProduct.getId_product());

                String upcProm;

                if (existingProm.isEmpty()) {
                    long randomNum = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
                    upcProm = randomNum + "P";

                    try {
                        jdbcTemplate.update(
                                """
                                INSERT INTO store_product (UPC,
                                                           UPC_prom,
                                                           id_product,
                                                           selling_price,
                                                           products_number,
                                                           promotional_product)
                                VALUES (?, ?, ?, ?, ?, ?)
                                """,
                                upcProm,
                                null,
                                storeProduct.getId_product(),
                                priceWithVatAndDiscount,
                                requestDto.getQuantity(),
                                true
                        );

                    } catch (DataIntegrityViolationException | UncategorizedSQLException e) {
                        throw new InvalidProductException(
                                "Invalid product or UPC reference: " + storeProduct.getId_product());
                    }

                } else {
                    upcProm = existingProm.get().getUPC();

                    jdbcTemplate.update(
                            """
                                UPDATE store_product
                                SET selling_price = ?, products_number = ?
                                WHERE UPC = ?
                                """,
                            priceWithVatAndDiscount,
                            existingProm.get().getProducts_number() + requestDto.getQuantity(),
                            upcProm
                    );
                }

                jdbcTemplate.update(
                        """
                            UPDATE store_product
                            SET UPC_prom = ?
                            WHERE UPC = ?
                            """,
                            upcProm,
                            storeProduct.getUPC()
                );
            }

            return storeProductRepository.findAllInfoByUPC(storeProduct.getUPC())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Store product not found: " + storeProduct.getUPC()));

        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductException(
                    "Invalid product or UPC reference: " + requestDto.getUPC()
            );
        }
    }

    private boolean checkPromotional(int totalQuantity, Date expiringDate) {
        LocalDate today = LocalDate.now();
        LocalDate expiry = expiringDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long daysToExpiry = ChronoUnit.DAYS.between(today, expiry);
        return daysToExpiry <= DAYS_BEFORE_EXPIRY && totalQuantity >= MIN_QUANTITY_FOR_PROMOTION;
    }

    @Transactional
    public void deleteExpired() {
        var expiredBatches = jdbcTemplate.query(
                """
                SELECT UPC, quantity
                FROM batch
                WHERE expiring_date < CURRENT_DATE
                """,
                (rs, rowNum) -> new Object[]{
                        rs.getString("UPC"),
                        rs.getInt("quantity")
                }
        );
        for (Object[] batch : expiredBatches) {
            String upc = (String) batch[0];
            int quantity = (int) batch[1];
            jdbcTemplate.update(
                    """
                        UPDATE store_product
                        SET products_number = products_number - ?
                        WHERE UPC = ?
                        """,
                    quantity,
                    upc
            );
        }
        jdbcTemplate.update(
                """
                    DELETE
                    FROM batch
                    WHERE expiring_date < CURRENT_DATE
                    """
        );
    }
}
