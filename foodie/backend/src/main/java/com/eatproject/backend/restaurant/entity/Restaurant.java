package com.eatproject.backend.restaurant.entity;

import com.eatproject.backend.common.CategoryType;
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
@SequenceGenerator(
        name = "rest_seq",
        sequenceName = "RESTAURANT_SEQ", // DB에 생성될 시퀀스 이름
        allocationSize = 50 // 메모리에 미리 할당할 ID 개수 (성능 핵심)
)
// 🚨 무한 루프 방지: 출력이나 비교를 할 때 밑에 달린 1:N 리스트들은 무시하도록 설정
@ToString(exclude = {"menus", "images", "tags"})
public class Restaurant {

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rest_seq")
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