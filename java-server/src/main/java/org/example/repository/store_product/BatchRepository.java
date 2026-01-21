package org.example.repository.store_product;

import lombok.RequiredArgsConstructor;
import org.example.dto.store_product.BatchRequestDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.InvalidProductException;
import org.example.model.store_product.StoreProduct;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RequiredArgsConstructor
@Repository
public class BatchRepository {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.20");
    private static final int MIN_QUANTITY_FOR_PROMOTION = 10;
    private static final int DAYS_BEFORE_EXPIRY = 5;
    private static final BigDecimal PROMO_RATE = new BigDecimal("0.8");

    private final JdbcTemplate jdbcTemplate;
    private final StoreProductRepository storeProductRepository;

    public StoreProduct save(BatchRequestDto requestDto) {
        BigDecimal priceWithVat = requestDto.getPrice()
                .multiply(BigDecimal.ONE.add(VAT_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        try {
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
            StoreProduct storeProduct = storeProductRepository.findByUPC(requestDto.getUPC())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Store product not found: " + requestDto.getUPC()
                    ));
            int updatedQuantity = storeProduct.getProducts_number() + requestDto.getQuantity();
            boolean shouldBePromotional = checkPromotional(updatedQuantity, requestDto.getExpiring_date());
            BigDecimal finalPrice = shouldBePromotional
                    ? priceWithVat.multiply(PROMO_RATE).setScale(2, RoundingMode.HALF_UP)
                    : priceWithVat;
            storeProductRepository.updateProductPriceAndPromotion(
                    storeProduct.getUPC(),
                    finalPrice,
                    shouldBePromotional
            );
            jdbcTemplate.update(
                    "UPDATE batch SET selling_price = ? WHERE UPC = ?",
                    finalPrice,
                    storeProduct.getUPC()
            );
            jdbcTemplate.update(
                    "UPDATE store_product SET products_number = ? WHERE UPC = ?",
                    updatedQuantity,
                    storeProduct.getUPC()
            );
            return storeProductRepository.findByUPC(storeProduct.getUPC()).get();
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
                    "UPDATE store_product SET products_number = products_number - ? WHERE UPC = ?",
                    quantity,
                    upc
            );
        }
        jdbcTemplate.update(
                "DELETE FROM batch WHERE expiring_date < CURRENT_DATE"
        );
    }
}
