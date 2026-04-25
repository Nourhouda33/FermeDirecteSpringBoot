// src/main/java/com/FermeDirecte/FermeDirecte/entity/Coupon.java
package com.FermeDirecte.FermeDirecte.entity;

import com.FermeDirecte.FermeDirecte.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valeur;

    @Column(nullable = false)
    private LocalDateTime dateExpiration;

    @Column(nullable = false)
    private Integer usagesMax;

    @Column(nullable = false)
    @Builder.Default
    private Integer usagesActuels = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
