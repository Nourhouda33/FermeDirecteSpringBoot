// src/main/java/com/FermeDirecte/FermeDirecte/repository/OrderRepository.java
package com.FermeDirecte.FermeDirecte.repository;

import com.FermeDirecte.FermeDirecte.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient_Id(Long clientId);
    List<Order> findByClient_IdOrderByDateCommandeDesc(Long clientId);
}
