package com.eatproject.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESTAURANT_IMAGES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "image_seq",
        sequenceName = "RESTAURANT_IMAGE_SEQ",
        allocationSize = 50 // 이미지 역시 한 번에 여러 장 올라오므로 효율적입니다.
)
@ToString(exclude = {"restaurant"}) // 무한 루프 방지
public class RestaurantImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
    @Column(name = "IMG_ID")
    private Long imgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REST_ID", nullable = false)
    private Restaurant restaurant;


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