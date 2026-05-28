package com.eatproject.backend.restaurant.entity;


import com.eatproject.backend.restaurant.dto.RestaurantDto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.davidmoten.geo.GeoHash; // 지오해쉬
import org.hibernate.annotations.BatchSize;

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
@ToString(exclude = {"menus", "images", "restaurantTag"})
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rest_seq")
    @Column(name = "REST_ID")
    private Integer restId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID") // DB의 TAG_ID 컬럼(FK)과 연결
    private RestaurantTag restaurantTag;

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

    // 🌟 [신규 추가 필드]
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "BUSINESS_HOURS", length = 255)
    private String businessHours;

    @Column(name = "CLOSED_DAYS", length = 255)
    private String closedDays;

    @Column(name = "SNS_URL", length = 255)
    private String snsUrl;

    @Column(name = "LAST_SYNC_AT", nullable = false)
    @Builder.Default
    private LocalDateTime lastSyncAt = LocalDateTime.now();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // --- [연관 관계 설정] ---
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<RestaurantImage> images = new HashSet<>();


    // --- [편의 메서드: 양방향 관계를 위해 필수] ---

    public void addMenu(Menu menu) {
        this.menus.add(menu);
        menu.setRestaurant(this);
    }

    public void addImage(RestaurantImage image) {
        this.images.add(image);
        image.setRestaurant(this);
    }

    // [수정] 이제 Tag는 List가 아니므로 편의 메서드 대신 setter를 사용하거나 아래와 같이 작성합니다.
    public void setCategoryTag(RestaurantTag tag) {
        this.restaurantTag = tag;
        // 필요 시 부모 쪽 리스트에도 추가 (양방향인 경우)
        if (!tag.getRestaurants().contains(this)) {
            tag.getRestaurants().add(this);
        }
    }

    @PrePersist
    @PreUpdate
    public void generateGeohash() {
        if (this.lat != null && this.lng != null) {
            this.geohash = GeoHash.encodeHash(this.lat.doubleValue(), this.lng.doubleValue(), 10);
        }
    }
    // Restaurant.java 내부
    public RestaurantDto toFullDto() {
        return RestaurantDto.builder()
                .restId(this.restId)
                .name(this.name)
                .address(this.address)
                .lat(this.lat)
                .lng(this.lng)
                .geohash(this.geohash)
                .avgPrice(this.avgPrice)
                .minPrice(this.minPrice)
                .maxPrice(this.maxPrice)
                .description(this.description)
                .phone(this.phone)
                .businessHours(this.businessHours)
                .closedDays(this.closedDays)
                .snsUrl(this.snsUrl)
                .createdAt(this.createdAt)
                .category(this.restaurantTag != null ? this.restaurantTag.getCategory().name() : null)
                .customTag(this.restaurantTag != null ? "#" + this.restaurantTag.getCustomTag() : null)
                // 메뉴 매핑
                .menus(this.menus.stream()
                        .filter(m -> m.getDeletedAt() == null)
                        .map(m -> RestaurantDto.MenuResponseDto.builder()
                                .menuId(m.getMenuId())
                                .pName(m.getPName())
                                .price(m.getPrice())
                                .isRepresentative(m.getIsRepresentative())
                                .build()).collect(Collectors.toList()))
                // 이미지 매핑
                .images(this.images.stream()
                        .filter(i -> i.getDeletedAt() == null)
                        .map(i -> RestaurantDto.ImageResponseDto.builder()
                                .imgId(i.getImgId())
                                .imgUrl(i.getImgUrl())
                                .thumbUrl(i.getThumbUrl())
                                .category(i.getCategory())
                                .isMain(i.getIsMain())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}