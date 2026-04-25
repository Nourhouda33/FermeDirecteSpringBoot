// src/main/java/com/FermeDirecte/FermeDirecte/dto/dashboard/SellerDashboardResponse.java
package com.FermeDirecte.FermeDirecte.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SellerDashboardResponse {

    private Long totalProduits;
    private Long commandesEnAttente;
    private BigDecimal revenuTotal;
    private List<Map<String, Object>> stockFaible;
}
