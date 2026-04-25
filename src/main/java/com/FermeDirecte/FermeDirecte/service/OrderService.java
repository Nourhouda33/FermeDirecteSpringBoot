// src/main/java/com/FermeDirecte/FermeDirecte/service/OrderService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.order.*;
import com.FermeDirecte.FermeDirecte.entity.*;
import com.FermeDirecte.FermeDirecte.enums.OrderStatus;
import com.FermeDirecte.FermeDirecte.enums.PaymentStatus;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public OrderResponse passerCommande(OrderRequest request, String email) {
        User user = getUser(email);

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Panier vide", HttpStatus.BAD_REQUEST));

        if (cart.getLignes().isEmpty()) {
            throw new BusinessException("Panier vide", HttpStatus.BAD_REQUEST);
        }

        Address adresse = user.getAdresses().stream()
                .filter(a -> a.getId().equals(request.getAdresseId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Adresse introuvable", HttpStatus.NOT_FOUND));

        // Vérification stock
        for (CartItem item : cart.getLignes()) {
            if (item.getProduit().getStock() < item.getQuantite()) {
                throw new BusinessException(
                        "Stock insuffisant pour : " + item.getProduit().getNom(), HttpStatus.BAD_REQUEST);
            }
        }

        // Calcul sousTotal
        BigDecimal sousTotal = cart.getLignes().stream().map(item -> {
            BigDecimal prix = item.getProduit().getPrix();
            if (item.getVariante() != null) prix = prix.add(item.getVariante().getPrixDelta());
            return prix.multiply(BigDecimal.valueOf(item.getQuantite()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Coupon
        Coupon coupon = null;
        BigDecimal remise = BigDecimal.ZERO;
        if (request.getCodeCoupon() != null && !request.getCodeCoupon().isBlank()) {
            coupon = couponRepository.findByCodeAndActifTrue(request.getCodeCoupon())
                    .orElseThrow(() -> new BusinessException("Coupon invalide", HttpStatus.BAD_REQUEST));
            switch (coupon.getType()) {
                case PERCENT -> remise = sousTotal.multiply(coupon.getValeur()).divide(BigDecimal.valueOf(100));
                case FIXED -> remise = coupon.getValeur();
            }
            coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
        }

        BigDecimal fraisLivraison = BigDecimal.valueOf(5.0);
        BigDecimal totalTTC = sousTotal.subtract(remise).add(fraisLivraison);

        // Créer commande
        Order order = Order.builder()
                .client(user)
                .adresseLivraison(adresse)
                .coupon(coupon)
                .numeroCommande("ORD-" + Year.now().getValue() + "-" + String.format("%05d", new Random().nextInt(99999)))
                .statut(OrderStatus.PENDING)
                .statutPaiement(PaymentStatus.PENDING)
                .sousTotal(sousTotal)
                .fraisLivraison(fraisLivraison)
                .totalTTC(totalTTC)
                .build();

        // Lignes de commande + décrémenter stock
        List<OrderItem> lignes = cart.getLignes().stream().map(item -> {
            BigDecimal prix = item.getProduit().getPrix();
            if (item.getVariante() != null) prix = prix.add(item.getVariante().getPrixDelta());
            item.getProduit().setStock(item.getProduit().getStock() - item.getQuantite());
            return OrderItem.builder()
                    .commande(order)
                    .produit(item.getProduit())
                    .variante(item.getVariante())
                    .quantite(item.getQuantite())
                    .prixUnitaire(prix)
                    .build();
        }).collect(Collectors.toList());

        order.getLignes().addAll(lignes);
        orderRepository.save(order);

        // Vider le panier
        cart.getLignes().clear();
        cartRepository.save(cart);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande introuvable", HttpStatus.NOT_FOUND));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> mesCommandes(String email) {
        User user = getUser(email);
        return orderRepository.findByClient_IdOrderByDateCommandeDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> toutesCommandes() {
        return orderRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse mettreAJourStatut(Long id, OrderStatus statut) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande introuvable", HttpStatus.NOT_FOUND));
        order.setStatut(statut);
        if (statut == OrderStatus.PAID) order.setStatutPaiement(PaymentStatus.PAID);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse annuler(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande introuvable", HttpStatus.NOT_FOUND));

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException("Impossible d'annuler cette commande", HttpStatus.BAD_REQUEST);
        }

        order.setStatut(OrderStatus.CANCELLED);
        if (order.getStatutPaiement() == PaymentStatus.PAID) {
            order.setStatutPaiement(PaymentStatus.REFUNDED);
        }

        // Remettre le stock
        order.getLignes().forEach(item ->
                item.getProduit().setStock(item.getProduit().getStock() + item.getQuantite()));

        return toResponse(orderRepository.save(order));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> lignes = o.getLignes().stream().map(item ->
                OrderItemResponse.builder()
                        .nomProduit(item.getProduit().getNom())
                        .infoVariante(item.getVariante() != null
                                ? item.getVariante().getAttribut() + " : " + item.getVariante().getValeur() : null)
                        .quantite(item.getQuantite())
                        .prixUnitaire(item.getPrixUnitaire())
                        .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .numeroCommande(o.getNumeroCommande())
                .statut(o.getStatut())
                .statutPaiement(o.getStatutPaiement())
                .lignes(lignes)
                .sousTotal(o.getSousTotal())
                .fraisLivraison(o.getFraisLivraison())
                .totalTTC(o.getTotalTTC())
                .dateCommande(o.getDateCommande())
                .build();
    }
}
