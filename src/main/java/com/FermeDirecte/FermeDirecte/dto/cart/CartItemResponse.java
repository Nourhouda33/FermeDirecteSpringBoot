// src/main/java/com/FermeDirecte/FermeDirecte/dto/cart/CartItemResponse.java
package com.FermeDirecte.FermeDirecte.dto.cart;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long produitId;
    private String nomProduit;
    private Long varianteId;
    private String infoVariante;
    private BigDecimal prix;
    private Integer quantite;
    private BigDecimal sousTotal;
}
