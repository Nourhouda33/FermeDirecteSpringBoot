// src/main/java/com/FermeDirecte/FermeDirecte/dto/category/CategoryResponse.java
package com.FermeDirecte.FermeDirecte.dto.category;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private List<CategoryResponse> sousCategories;
}
