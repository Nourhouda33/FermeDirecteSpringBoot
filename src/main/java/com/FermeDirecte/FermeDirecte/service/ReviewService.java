// src/main/java/com/FermeDirecte/FermeDirecte/service/ReviewService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.review.*;
import com.FermeDirecte.FermeDirecte.entity.*;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewResponse poster(ReviewRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));

        Product produit = productRepository.findById(request.getProduitId())
                .orElseThrow(() -> new BusinessException("Produit introuvable", HttpStatus.NOT_FOUND));

        // Vérifier achat
        boolean aAchete = orderRepository.findByClient_Id(user.getId()).stream()
                .flatMap(o -> o.getLignes().stream())
                .anyMatch(item -> item.getProduit().getId().equals(request.getProduitId()));

        if (!aAchete) {
            throw new BusinessException("Vous devez acheter ce produit avant de laisser un avis", HttpStatus.FORBIDDEN);
        }

        if (reviewRepository.existsByClient_IdAndProduit_Id(user.getId(), request.getProduitId())) {
            throw new BusinessException("Vous avez déjà laissé un avis pour ce produit", HttpStatus.CONFLICT);
        }

        Review review = Review.builder()
                .client(user)
                .produit(produit)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .approuve(false)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getParProduit(Long produitId) {
        return reviewRepository.findByProduit_IdAndApprouveTrue(produitId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse approuver(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Avis introuvable", HttpStatus.NOT_FOUND));
        review.setApprouve(true);
        return toResponse(reviewRepository.save(review));
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .nomClient(r.getClient().getPrenom() + " " + r.getClient().getNom())
                .note(r.getNote())
                .commentaire(r.getCommentaire())
                .dateCreation(r.getDateCreation())
                .approuve(r.getApprouve())
                .build();
    }
}
