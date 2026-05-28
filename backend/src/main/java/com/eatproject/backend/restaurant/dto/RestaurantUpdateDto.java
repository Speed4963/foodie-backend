package com.eatproject.backend.restaurant.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RestaurantUpdateDto {
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;

    // 🌟 [신규 추가 필드]
    private String description;
    private String phone;
    private String businessHours;
    private String closedDays;
    private String snsUrl;

    private List<MenuUpdateDto> menus;
    private List<ImageUpdateDto> images;

    private Integer tagId;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuUpdateDto {
        private Integer menuId;
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageUpdateDto {
        private Long imgId;
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
        private Integer displayOrder;
    }
}