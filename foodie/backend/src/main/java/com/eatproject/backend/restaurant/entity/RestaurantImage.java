package com.eatproject.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESTAURANTS_IMAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"restaurant", "menu"}) // 무한 루프 방지
public class RestaurantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMG_ID")
    private Long imgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REST_ID", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @Column(name = "IMG_URL", nullable = false, length = 512)
    private String imgUrl;

    @Column(name = "THUMB_URL", length = 512)
    private String thumbUrl;

    @Column(name = "CATEGORY", nullable = false, length = 30)
    private String category;

    @Column(name = "IS_MAIN", nullable = false)
    private Boolean isMain = false;   //사용하려면 = FALSE; 지우기

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer displayOrder;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}