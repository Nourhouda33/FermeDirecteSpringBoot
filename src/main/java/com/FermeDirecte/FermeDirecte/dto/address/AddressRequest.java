// src/main/java/com/FermeDirecte/FermeDirecte/dto/address/AddressRequest.java
package com.FermeDirecte.FermeDirecte.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressRequest {

    @NotBlank
    private String rue;

    @NotBlank
    private String ville;

    @NotBlank
    private String codePostal;

    @NotBlank
    private String pays;

    private Boolean principal;
}
