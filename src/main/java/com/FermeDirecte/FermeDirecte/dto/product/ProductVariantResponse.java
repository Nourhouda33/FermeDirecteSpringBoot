// src/main/java/com/FermeDirecte/FermeDirecte/dto/product/ProductVariantResponse.java
package com.FermeDirecte.FermeDirecte.dto.product;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductVariantResponse {

    private Long id;
    private String attribut;
    private String valeur;
    private Integer stockSupplementaire;
    private BigDecimal prixDelta;
}
