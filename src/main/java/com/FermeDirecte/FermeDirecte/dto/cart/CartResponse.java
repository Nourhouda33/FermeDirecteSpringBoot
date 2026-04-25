// src/main/java/com/FermeDirecte/FermeDirecte/dto/cart/CartResponse.java
package com.FermeDirecte.FermeDirecte.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> lignes;
    private BigDecimal sousTotal;
    private BigDecimal remise;
    private String codeCoupon;
    private BigDecimal total;
}
