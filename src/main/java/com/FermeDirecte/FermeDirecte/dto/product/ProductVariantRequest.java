// src/main/java/com/FermeDirecte/FermeDirecte/dto/product/ProductVariantRequest.java
package com.FermeDirecte.FermeDirecte.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductVariantRequest {

    @NotBlank
    private String attribut;

    @NotBlank
    private String valeur;

    @NotNull @Min(0)
    private Integer stockSupplementaire;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal prixDelta;
}
