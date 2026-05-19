package com.eatproject.backend.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RestaurantCreateDto {
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

    private List<MenuCreateDto> menus;
    private List<ImageCreateDto> images;

    private Integer tagId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuCreateDto {
        @JsonProperty("pName") // 👈 JSON의 pName을 이 필드에 매핑
        private String pName;

        @JsonProperty("price")
        private Integer price;

        @JsonProperty("isRepresentative")
        private Boolean isRepresentative;
    }

        @Getter @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ImageCreateDto {
            @JsonProperty("imgUrl")
            private String imgUrl;

            @JsonProperty("thumbUrl")
            private String thumbUrl;

            @JsonProperty("category")
            private String category;

            @JsonProperty("isMain")
            private Boolean isMain;

            @JsonProperty("displayOrder")
            private Integer displayOrder;
        }
}