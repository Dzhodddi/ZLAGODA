package org.example.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product", nullable = false)
    private Long id_product;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "product_characteristics", nullable = false)
    private String productCharacteristics;
    @Column(name = "category_number", nullable = false)
    private int categoryNumber;
}
