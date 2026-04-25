// src/main/java/com/FermeDirecte/FermeDirecte/service/DashboardService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.dashboard.*;
import com.FermeDirecte.FermeDirecte.entity.Order;
import com.FermeDirecte.FermeDirecte.enums.OrderStatus;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        List<Order> commandes = orderRepository.findAll();

        BigDecimal ca = commandes.stream()
                .filter(o -> o.getStatut() != OrderStatus.CANCELLED)
                .map(Order::getTotalTTC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> commandesRecentes = commandes.stream()
                .sorted(Comparator.comparing(Order::getDateCommande, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", o.getId());
                    m.put("numeroCommande", o.getNumeroCommande());
                    m.put("statut", o.getStatut());
                    m.put("totalTTC", o.getTotalTTC());
                    return m;
                }).collect(Collectors.toList());

        return AdminDashboardResponse.builder()
                .totalUtilisateurs(userRepository.count())
                .totalCommandes((long) commandes.size())
                .totalProduits(productRepository.count())
                .chiffreAffairesGlobal(ca)
                .topProduits(Collections.emptyList())
                .commandesRecentes(commandesRecentes)
                .build();
    }

    @Transactional(readOnly = true)
    public SellerDashboardResponse getSellerDashboard(String email) {
        var profile = sellerProfileRepository.findByUser_Email(email)
                .orElseThrow(() -> new BusinessException("Profil vendeur introuvable", HttpStatus.NOT_FOUND));

        long totalProduits = profile.getProduits().size();

        long commandesEnAttente = orderRepository.findAll().stream()
                .filter(o -> o.getStatut() == OrderStatus.PENDING)
                .flatMap(o -> o.getLignes().stream())
                .filter(item -> item.getProduit().getSellerProfile().getId().equals(profile.getId()))
                .map(item -> item.getCommande().getId())
                .distinct().count();

        BigDecimal revenu = orderRepository.findAll().stream()
                .filter(o -> o.getStatut() != OrderStatus.CANCELLED)
                .flatMap(o -> o.getLignes().stream())
                .filter(item -> item.getProduit().getSellerProfile().getId().equals(profile.getId()))
                .map(item -> item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> stockFaible = profile.getProduits().stream()
                .filter(p -> p.getStock() < 10)
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("nom", p.getNom());
                    m.put("stock", p.getStock());
                    return m;
                }).collect(Collectors.toList());

        return SellerDashboardResponse.builder()
                .totalProduits(totalProduits)
                .commandesEnAttente(commandesEnAttente)
                .revenuTotal(revenu)
                .stockFaible(stockFaible)
                .build();
    }
}
