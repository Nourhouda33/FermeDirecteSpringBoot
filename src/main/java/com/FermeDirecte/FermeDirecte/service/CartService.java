// src/main/java/com/FermeDirecte/FermeDirecte/service/CartService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.cart.*;
import com.FermeDirecte.FermeDirecte.entity.*;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse getPanier(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        return toResponse(cart, null);
    }

    @Transactional
    public CartResponse ajouterArticle(CartItemRequest request, String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        Product produit = productRepository.findById(request.getProduitId())
                .orElseThrow(() -> new BusinessException("Produit introuvable", HttpStatus.NOT_FOUND));

        if (produit.getStock() < request.getQuantite()) {
            throw new BusinessException("Stock insuffisant", HttpStatus.BAD_REQUEST);
        }

        // Vérifier si l'article existe déjà
        cart.getLignes().stream()
                .filter(item -> item.getProduit().getId().equals(request.getProduitId())
                        && (request.getVarianteId() == null || (item.getVariante() != null
                        && item.getVariante().getId().equals(request.getVarianteId()))))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantite(item.getQuantite() + request.getQuantite()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .panier(cart)
                                    .produit(produit)
                                    .quantite(request.getQuantite())
                                    .build();
                            cart.getLignes().add(newItem);
                        }
                );

        cartRepository.save(cart);
        return toResponse(cart, null);
    }

    @Transactional
    public CartResponse modifierQuantite(Long itemId, Integer quantite, String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Panier introuvable", HttpStatus.NOT_FOUND));

        CartItem item = cart.getLignes().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Article introuvable", HttpStatus.NOT_FOUND));

        if (item.getProduit().getStock() < quantite) {
            throw new BusinessException("Stock insuffisant", HttpStatus.BAD_REQUEST);
        }

        item.setQuantite(quantite);
        cartRepository.save(cart);
        return toResponse(cart, null);
    }

    @Transactional
    public CartResponse retirerArticle(Long itemId, String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Panier introuvable", HttpStatus.NOT_FOUND));

        cart.getLignes().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
        return toResponse(cart, null);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    private CartResponse toResponse(Cart cart, String codeCoupon) {
        List<CartItemResponse> lignes = cart.getLignes().stream().map(item -> {
            BigDecimal prix = item.getProduit().getPrix();
            if (item.getVariante() != null) {
                prix = prix.add(item.getVariante().getPrixDelta());
            }
            BigDecimal sousTotal = prix.multiply(BigDecimal.valueOf(item.getQuantite()));
            return CartItemResponse.builder()
                    .id(item.getId())
                    .produitId(item.getProduit().getId())
                    .nomProduit(item.getProduit().getNom())
                    .varianteId(item.getVariante() != null ? item.getVariante().getId() : null)
                    .infoVariante(item.getVariante() != null
                            ? item.getVariante().getAttribut() + " : " + item.getVariante().getValeur() : null)
                    .prix(prix)
                    .quantite(item.getQuantite())
                    .sousTotal(sousTotal)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal sousTotal = lignes.stream()
                .map(CartItemResponse::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .lignes(lignes)
                .sousTotal(sousTotal)
                .remise(BigDecimal.ZERO)
                .codeCoupon(codeCoupon)
                .total(sousTotal)
                .build();
    }
}
