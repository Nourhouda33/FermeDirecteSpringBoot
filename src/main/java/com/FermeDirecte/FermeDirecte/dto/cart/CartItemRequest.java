// src/main/java/com/FermeDirecte/FermeDirecte/dto/cart/CartItemRequest.java
package com.FermeDirecte.FermeDirecte.dto.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItemRequest {

    @NotNull
    private Long produitId;

    private Long varianteId;

    @NotNull @Min(1)
    private Integer quantite;
}
