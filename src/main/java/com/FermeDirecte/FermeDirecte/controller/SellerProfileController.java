// src/main/java/com/FermeDirecte/FermeDirecte/controller/SellerProfileController.java
package com.FermeDirecte.FermeDirecte.controller;

import com.FermeDirecte.FermeDirecte.dto.seller.*;
import com.FermeDirecte.FermeDirecte.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
@Tag(name = "Vendeurs")
@SecurityRequirement(name = "bearerAuth")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProfileResponse> getMonProfil(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sellerProfileService.getMonProfil(userDetails.getUsername()));
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProfileResponse> creerOuModifier(
            @Valid @RequestBody SellerProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerProfileService.creerOuModifier(request, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerProfileService.getById(id));
    }
}
