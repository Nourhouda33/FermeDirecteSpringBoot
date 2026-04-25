// src/main/java/com/FermeDirecte/FermeDirecte/controller/ReviewController.java
package com.FermeDirecte.FermeDirecte.controller;

import com.FermeDirecte.FermeDirecte.dto.review.*;
import com.FermeDirecte.FermeDirecte.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> poster(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.poster(request, userDetails.getUsername()));
    }

    @GetMapping("/product/{produitId}")
    public ResponseEntity<List<ReviewResponse>> getParProduit(@PathVariable Long produitId) {
        return ResponseEntity.ok(reviewService.getParProduit(produitId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approuver(id));
    }
}
