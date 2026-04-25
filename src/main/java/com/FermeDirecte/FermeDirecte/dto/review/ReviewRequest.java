// src/main/java/com/FermeDirecte/FermeDirecte/dto/review/ReviewRequest.java
package com.FermeDirecte.FermeDirecte.dto.review;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewRequest {

    @NotNull
    private Long produitId;

    @NotNull @Min(1) @Max(5)
    private Integer note;

    private String commentaire;
}
