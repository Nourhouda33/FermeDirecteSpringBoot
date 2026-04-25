// src/main/java/com/FermeDirecte/FermeDirecte/controller/CartController.java
package com.FermeDirecte.FermeDirecte.controller;

import com.FermeDirecte.FermeDirecte.dto.cart.*;
import com.FermeDirecte.FermeDirecte.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Panier")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getPanier(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getPanier(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> ajouter(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.ajouterArticle(request, userDetails.getUsername()));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> modifier(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.modifierQuantite(itemId, body.get("quantite"), userDetails.getUsername()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> retirer(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.retirerArticle(itemId, userDetails.getUsername()));
    }
}
