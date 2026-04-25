// src/main/java/com/FermeDirecte/FermeDirecte/service/ProductService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.product.*;
import com.FermeDirecte.FermeDirecte.entity.*;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> listerProduits(Pageable pageable) {
        return productRepository.findByActifTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit introuvable", HttpStatus.NOT_FOUND));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> rechercher(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> filtrerParPrix(BigDecimal min, BigDecimal max, Pageable pageable) {
        return productRepository.findByPrixBetweenAndActifTrue(min, max, pageable).map(this::toResponse);
    }

    @Transactional
    public ProductResponse creer(ProductRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));

        SellerProfile seller = user.getSellerProfile() != null ? user.getSellerProfile() : null;
        if (seller == null) {
            throw new BusinessException("Profil vendeur introuvable", HttpStatus.FORBIDDEN);
        }

        Product product = Product.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .actif(request.getActif())
                .imageUrl(request.getImageUrl())
                .sellerProfile(seller)
                .build();

        productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse modifier(Long id, ProductRequest request, String email) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit introuvable", HttpStatus.NOT_FOUND));

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());
        product.setActif(request.getActif());
        product.setImageUrl(request.getImageUrl());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void desactiver(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit introuvable", HttpStatus.NOT_FOUND));
        product.setActif(false);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product p) {
        double noteMoyenne = p.getAvis().stream()
                .mapToInt(r -> r.getNote())
                .average().orElse(0.0);

        List<String> categories = p.getCategories().stream()
                .map(Category::getNom).collect(Collectors.toList());

        List<ProductVariantResponse> variantes = p.getVariantes().stream()
                .map(v -> ProductVariantResponse.builder()
                        .id(v.getId())
                        .attribut(v.getAttribut())
                        .valeur(v.getValeur())
                        .stockSupplementaire(v.getStockSupplementaire())
                        .prixDelta(v.getPrixDelta())
                        .build())
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .description(p.getDescription())
                .prix(p.getPrix())
                .prixPromo(p.getPrixPromo())
                .stock(p.getStock())
                .actif(p.getActif())
                .imageUrl(p.getImageUrl())
                .nomVendeur(p.getSellerProfile().getNomBoutique())
                .categories(categories)
                .variantes(variantes)
                .noteMoyenne(noteMoyenne)
                .build();
    }
}
