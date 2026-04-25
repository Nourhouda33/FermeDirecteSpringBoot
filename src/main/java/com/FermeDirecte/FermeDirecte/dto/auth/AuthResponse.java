// src/main/java/com/FermeDirecte/FermeDirecte/dto/auth/AuthResponse.java
package com.FermeDirecte.FermeDirecte.dto.auth;

import com.FermeDirecte.FermeDirecte.enums.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private Role role;
}
