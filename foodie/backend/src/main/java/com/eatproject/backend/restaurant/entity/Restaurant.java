package com.eatproject.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESTAURANTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REST_ID")
    private Integer restId;

    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @Column(name = "LAT", precision = 10, scale = 8, nullable = false)
    private BigDecimal lat;

    @Column(name = "LNG", precision = 11, scale = 8, nullable = false)
    private BigDecimal lng;

    @Column(name = "GEOHASH", length = 20, nullable = false)
    private String geohash;

    @Column(name = "ADDRESS", length = 255, nullable = false)
    private String address;

    @Column(name = "AVG_PRICE", nullable = false)
    @Builder.Default
    private Integer avgPrice = 0;

    @Column(name = "LAST_SYNC_AT", nullable = false)
    @Builder.Default
    private LocalDateTime lastSyncAt = LocalDateTime.now();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}