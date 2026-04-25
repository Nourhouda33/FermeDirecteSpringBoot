// src/main/java/com/FermeDirecte/FermeDirecte/dto/product/ProductResponse.java
package com.FermeDirecte.FermeDirecte.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private BigDecimal prixPromo;
    private Integer stock;
    private Boolean actif;
    private String imageUrl;
    private String nomVendeur;
    private List<String> categories;
    private List<ProductVariantResponse> variantes;
    private Double noteMoyenne;
}
