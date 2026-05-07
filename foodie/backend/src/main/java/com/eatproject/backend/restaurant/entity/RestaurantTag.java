package com.eatproject.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RESTAURANT_TAGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "restaurant") // 무한 루프 방지
public class RestaurantTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID")
    private Integer tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REST_ID", nullable = false)
    private Restaurant restaurant;

    @Column(name = "CATEGORY", nullable = false, length = 30)
    private String category;

    @Column(name = "CUSTOM_TAG", length = 100)
    private String customTag;
}