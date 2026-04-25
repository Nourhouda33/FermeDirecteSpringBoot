// src/main/java/com/FermeDirecte/FermeDirecte/dto/dashboard/AdminDashboardResponse.java
package com.FermeDirecte.FermeDirecte.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardResponse {

    private Long totalUtilisateurs;
    private Long totalCommandes;
    private Long totalProduits;
    private BigDecimal chiffreAffairesGlobal;
    private List<Map<String, Object>> topProduits;
    private List<Map<String, Object>> commandesRecentes;
}
