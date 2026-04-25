// src/main/java/com/FermeDirecte/FermeDirecte/dto/seller/SellerProfileResponse.java
package com.FermeDirecte.FermeDirecte.dto.seller;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SellerProfileResponse {

    private Long id;
    private String nomBoutique;
    private String description;
    private String logo;
    private Double note;
    private String emailVendeur;
}
