// src/main/java/com/FermeDirecte/FermeDirecte/dto/user/UserResponse.java
package com.FermeDirecte.FermeDirecte.dto.user;

import com.FermeDirecte.FermeDirecte.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
}
