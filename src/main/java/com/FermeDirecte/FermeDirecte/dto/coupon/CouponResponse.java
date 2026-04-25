// src/main/java/com/FermeDirecte/FermeDirecte/dto/coupon/CouponResponse.java
package com.FermeDirecte.FermeDirecte.dto.coupon;

import com.FermeDirecte.FermeDirecte.enums.CouponType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponResponse {

    private Long id;
    private String code;
    private CouponType type;
    private BigDecimal valeur;
    private LocalDateTime dateExpiration;
    private Integer usagesActuels;
    private Boolean actif;
}
