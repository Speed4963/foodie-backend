package com.eatproject.backend.restaurant.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantUpdateDto {
    // 식당 본체 정보 수정
    private String name;
    private BigDecimal lat;
    private BigDecimal lng;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;

    private List<MenuUpdateDto> menus;
    private List<ImageUpdateDto> images;
    private List<TagUpdateDto> customTags;


    @Getter @Setter
    public static class MenuUpdateDto {
        private Integer menuId; // ID가 있으면 수정, 없으면 신규 추가
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    public static class ImageUpdateDto {
        private Long imgId;     // ID가 있으면 수정, 없으면 신규 추가
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
        private Integer displayOrder;
    }

    @Getter @Setter
    public static class TagUpdateDto {
        private Integer tagId;  // ID가 있으면 수정, 없으면 신규 추가
        private String category;
        private String customTag;
    }
}