// src/main/java/com/FermeDirecte/FermeDirecte/dto/product/ProductRequest.java
package com.FermeDirecte.FermeDirecte.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String nom;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal prix;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal prixPromo;

    @NotNull @Min(0)
    private Integer stock;

    @Builder.Default
    private Boolean actif = true;

    private String imageUrl;

    @NotEmpty
    private List<Long> categoryIds;
}
