// src/main/java/com/FermeDirecte/FermeDirecte/repository/ReviewRepository.java
package com.FermeDirecte.FermeDirecte.repository;

import com.FermeDirecte.FermeDirecte.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduit_IdAndApprouveTrue(Long produitId);
    boolean existsByClient_IdAndProduit_Id(Long clientId, Long produitId);
}
