// src/main/java/com/FermeDirecte/FermeDirecte/entity/ProductVariant.java
package com.FermeDirecte.FermeDirecte.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductVariant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @JsonBackReference("product-variants")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product produit;

    @Column(nullable = false)
    private String attribut;

    @Column(nullable = false)
    private String valeur;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockSupplementaire = 0;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal prixDelta = BigDecimal.ZERO;
}
