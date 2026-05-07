package com.eatproject.backend.restaurant.entity;

import com.eatproject.backend.common.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESTAURANTS")
@Getter
@Setter // 요청하신 Setter 추가!
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "rest_seq",
        sequenceName = "RESTAURANT_SEQ", // DB에 생성될 시퀀스 이름
        allocationSize = 50 // 메모리에 미리 할당할 ID 개수 (성능 핵심)
)
// 🚨 무한 루프 방지: 출력이나 비교를 할 때 밑에 달린 1:N 리스트들은 무시하도록 설정
@ToString(exclude = {"menus", "images", "tags"})
public class Restaurant {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rest_seq")
    @Column(name = "REST_ID")
    private Integer restId;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "LAT", nullable = false, precision = 13, scale = 10)
    private BigDecimal lat;

    @Column(name = "LNG", nullable = false, precision = 13, scale = 10)
    private BigDecimal lng;

    @Column(name = "GEOHASH", nullable = false, length = 20)
    private String geohash;

    @Column(name = "AVG_PRICE", nullable = false)
    private Integer avgPrice;

    @Column(name = "MIN_PRICE")
    private Integer minPrice;

    @Column(name = "MAX_PRICE")
    private Integer maxPrice;

    @Column(name = "LAST_SYNC_AT", nullable = false)
    private LocalDateTime lastSyncAt;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // --- 연관관계 매핑 (Cascade 적용) ---
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantTag> tags = new ArrayList<>();

    // --- 연관관계 편의 메서드 (이건 Setter가 있어도 양방향을 위해 남겨두는 것이 좋습니다) ---
    public void addMenu(Menu menu) {
        this.menus.add(menu);
        menu.setRestaurant(this); // 자식 엔티티의 Setter 활용
    }

    public void addImage(RestaurantImage image) {
        this.images.add(image);
        image.setRestaurant(this); // 자식 엔티티의 Setter 활용
    }

    public void addRestaurantTag(RestaurantTag tag) {
        this.tags.add(tag);
        tag.setRestaurant(this); // 자식 엔티티의 Setter 활용
    }
}