// src/main/java/com/FermeDirecte/FermeDirecte/dto/coupon/CouponRequest.java
package com.FermeDirecte.FermeDirecte.dto.coupon;

import com.FermeDirecte.FermeDirecte.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponRequest {

    @NotBlank
    private String code;

    @NotNull
    private CouponType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal valeur;

    @NotNull @Future
    private LocalDateTime dateExpiration;

    @NotNull @Min(1)
    private Integer usagesMax;
}
