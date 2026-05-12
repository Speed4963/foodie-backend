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
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private Integer avgPrice;
    private Integer minPrice;
    private Integer maxPrice;

    private List<MenuUpdateDto> menus;
    private List<ImageUpdateDto> images;

    private Integer tagId;

    @Getter @Setter
    public static class MenuUpdateDto {
        private Integer menuId;
        private String pName;
        private Integer price;
        private Boolean isRepresentative;
    }

    @Getter @Setter
    public static class ImageUpdateDto {
        private Long imgId;
        private String imgUrl;
        private String thumbUrl;
        private String category;
        private Boolean isMain;
        private Integer displayOrder;
    }


}