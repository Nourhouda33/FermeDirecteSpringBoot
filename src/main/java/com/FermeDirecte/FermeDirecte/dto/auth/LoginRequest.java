// src/main/java/com/FermeDirecte/FermeDirecte/dto/auth/LoginRequest.java
package com.FermeDirecte.FermeDirecte.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @NotBlank @Email
    private String email;

    @NotBlank
    private String motDePasse;
}
