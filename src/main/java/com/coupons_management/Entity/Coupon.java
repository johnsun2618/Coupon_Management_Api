package com.coupons_management.Entity;

import com.coupons_management.Enum.CouponType;
import com.coupons_management.Service.JsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    // Getter for details
    @Getter
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> details; // updated from Object to Map<String, Object>

    private LocalDateTime expirationDate;

}
