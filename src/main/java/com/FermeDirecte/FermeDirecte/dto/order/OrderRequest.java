// src/main/java/com/FermeDirecte/FermeDirecte/dto/order/OrderRequest.java
package com.FermeDirecte.FermeDirecte.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderRequest {

    @NotNull
    private Long adresseId;

    private String codeCoupon;
}
