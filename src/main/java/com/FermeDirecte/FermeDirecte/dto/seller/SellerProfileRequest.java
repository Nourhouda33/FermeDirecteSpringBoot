// src/main/java/com/FermeDirecte/FermeDirecte/dto/seller/SellerProfileRequest.java
package com.FermeDirecte.FermeDirecte.dto.seller;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SellerProfileRequest {

    @NotBlank
    private String nomBoutique;

    private String description;

    private String logo;
}
