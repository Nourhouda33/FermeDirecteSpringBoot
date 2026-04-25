// src/main/java/com/FermeDirecte/FermeDirecte/dto/review/ReviewResponse.java
package com.FermeDirecte.FermeDirecte.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private String nomClient;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;
    private Boolean approuve;
}
