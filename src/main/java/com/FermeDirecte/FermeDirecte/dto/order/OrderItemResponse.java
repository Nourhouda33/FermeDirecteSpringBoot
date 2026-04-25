// src/main/java/com/FermeDirecte/FermeDirecte/dto/order/OrderItemResponse.java
package com.FermeDirecte.FermeDirecte.dto.order;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItemResponse {

    private String nomProduit;
    private String infoVariante;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
