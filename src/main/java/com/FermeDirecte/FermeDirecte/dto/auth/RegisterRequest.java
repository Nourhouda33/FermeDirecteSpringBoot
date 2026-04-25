// src/main/java/com/FermeDirecte/FermeDirecte/dto/auth/RegisterRequest.java
package com.FermeDirecte.FermeDirecte.dto.auth;

import com.FermeDirecte.FermeDirecte.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @Builder.Default
    private Role role = Role.CUSTOMER;
}
