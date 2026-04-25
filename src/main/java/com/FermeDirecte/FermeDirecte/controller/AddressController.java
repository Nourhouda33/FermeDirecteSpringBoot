// src/main/java/com/FermeDirecte/FermeDirecte/controller/AddressController.java
package com.FermeDirecte.FermeDirecte.controller;

import com.FermeDirecte.FermeDirecte.dto.address.*;
import com.FermeDirecte.FermeDirecte.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Tag(name = "Adresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMesAdresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getMesAdresses(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> ajouter(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.ajouter(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.modifier(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        addressService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
