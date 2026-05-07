package com.eatproject.backend.restaurant.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateDto {
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String geohash;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;

    private List<MenuCreateDto> menus;
    private List<ImageCreateDto> images;
    private List<TagCreateDto> customTags;


    @Getter @Setter
    public static class MenuCreateDto {
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    public static class ImageCreateDto {
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
        private Integer displayOrder;
    }

    @Getter @Setter
    public static class TagCreateDto {
        private String category;
        private String customTag;
    }
}