// src/main/java/com/FermeDirecte/FermeDirecte/dto/address/AddressResponse.java
package com.FermeDirecte.FermeDirecte.dto.address;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressResponse {

    private Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private Boolean principal;
}
