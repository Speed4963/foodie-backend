package com.eatproject.backend.restaurant.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.github.davidmoten.geo.GeoHash; // 지오해쉬

@Entity
@Table(name = "RESTAURANTS")
@Getter
@Setter // 서비스 로직에서 set 메서드를 사용하므로 추가합니다.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "rest_seq",
        sequenceName = "RESTAURANT_SEQ",
        allocationSize = 50
)
@ToString(exclude = {"menus", "images", "tags"})
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rest_seq")
    @Column(name = "REST_ID")
    private Integer restId;

    @Column(name = "NAME", length = 200, nullable = false)
    private String name;



    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "LAT", nullable = false, precision = 13, scale = 10)
    private BigDecimal lat;

    @Column(name = "LNG", nullable = false, precision = 13, scale = 10)
    private BigDecimal lng;

    @Column(name = "GEOHASH", length = 20, nullable = false)
    private String geohash;

    @Column(name = "AVG_PRICE", nullable = false)
    @Builder.Default
    private Integer avgPrice = 0;

    // [추가] 서비스 로직에서 사용 중인 최소/최대 가격
    @Column(name = "MIN_PRICE")
    private Integer minPrice;

    @Column(name = "MAX_PRICE")
    private Integer maxPrice;

    @Column(name = "LAST_SYNC_AT", nullable = false)
    @Builder.Default
    private LocalDateTime lastSyncAt = LocalDateTime.now();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // --- [연관 관계 설정] ---

    // 1. 메뉴 (Soft Delete 적용을 위해 Cascade 설정)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    // 2. 이미지 (Soft Delete 적용을 위해 Cascade 설정)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RestaurantImage> images = new ArrayList<>();

    // 3. 태그 (사용자 확인에 따라 이름은 tags, 메서드는 addRestaurantTag 사용)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RestaurantTag> tags = new ArrayList<>();


    // --- [편의 메서드: 양방향 관계를 위해 필수] ---

    public void addMenu(Menu menu) {
        this.menus.add(menu);
        menu.setRestaurant(this);
    }

    public void addImage(RestaurantImage image) {
        this.images.add(image);
        image.setRestaurant(this);
    }

    public void addRestaurantTag(RestaurantTag tag) {
        this.tags.add(tag);
        tag.setRestaurant(this);
    }
    @PrePersist
    @PreUpdate // 수정 시에도 위치가 바뀌면 갱신되도록 설정
    public void generateGeohash() {
        if (this.lat != null && this.lng != null) {
            // 위도, 경도를 기반으로 Geohash 생성 (보통 7~9자리면 동네 단위 검색에 충분합니다)
            // precision 10은 사용자님의 DB 컬럼 크기(20) 안에도 넉넉히 들어갑니다.
            this.geohash = GeoHash.encodeHash(this.lat.doubleValue(), this.lng.doubleValue(), 10);
        }
    }
}