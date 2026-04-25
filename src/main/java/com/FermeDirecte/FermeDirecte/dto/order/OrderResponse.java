// src/main/java/com/FermeDirecte/FermeDirecte/dto/order/OrderResponse.java
package com.FermeDirecte.FermeDirecte.dto.order;

import com.FermeDirecte.FermeDirecte.enums.OrderStatus;
import com.FermeDirecte.FermeDirecte.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String numeroCommande;
    private OrderStatus statut;
    private PaymentStatus statutPaiement;
    private List<OrderItemResponse> lignes;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private LocalDateTime dateCommande;
}
