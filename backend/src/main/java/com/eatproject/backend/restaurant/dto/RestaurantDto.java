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
    private LocalDateTime createdAt;


    private String category;   // Enum의 name값 (예: "VEGETARIAN")
    private String customTag;  // 부모 테이블의 커스텀 태그 (예: "#비건인증")

    private List<MenuResponseDto> menus;
    private List<ImageResponseDto> images;

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

}