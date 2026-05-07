package com.eatproject.backend.restaurant.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Integer restId;
    private String name;
    private String address; // 추가: 사용자에게 보여줄 주소 정보
    private BigDecimal lat;
    private BigDecimal lng;
    private String geohash;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;
    private LocalDateTime createdAt;
    private String category;

    private List<MenuResponseDto> menus;
    private List<ImageResponseDto> images;
    private List<TagResponseDto> tags;

    @Getter @Setter
    public static class MenuResponseDto {
        private Integer menuId;
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    public static class ImageResponseDto {
        private Long imgId;
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
    }

    @Getter @Setter
    public static class TagResponseDto {
        private Integer tagId;
        private String category;
        private String customTag;
    }
}