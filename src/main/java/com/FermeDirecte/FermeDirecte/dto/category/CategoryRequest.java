// src/main/java/com/FermeDirecte/FermeDirecte/dto/category/CategoryRequest.java
package com.FermeDirecte.FermeDirecte.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    private String nom;

    private String description;

    private Long parentId;
}
