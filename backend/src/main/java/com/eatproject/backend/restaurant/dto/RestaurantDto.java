package com.eatproject.backend.restaurant.dto;

import com.eatproject.backend.common.CategoryType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // 서비스 레이어 변환 편의를 위해 추가
@ToString
public class RestaurantDto {
    private Integer restId;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String geohash;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;

    // 🌟 [신규 추가 필드]
    private String description;
    private String phone;
    private String businessHours;
    private String closedDays;
    private String snsUrl;

    private LocalDateTime createdAt;

    private String category;   // Enum의 name값 (예: "VEGETARIAN")
    private String customTag;  // 부모 테이블의 커스텀 태그 (예: "#비건인증")

    private List<MenuResponseDto> menus;
    private List<ImageResponseDto> images;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuResponseDto {
        private Integer menuId;
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageResponseDto {
        private Long imgId;
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
    }
}