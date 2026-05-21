package com.eatproject.backend.restaurant.entity;

import com.eatproject.backend.common.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESTAURANT_TAGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "tag_seq",
        sequenceName = "RESTAURANT_TAG_SEQ",
        allocationSize = 50
)
@ToString(exclude = "restaurant") // 무한 루프 방지
public class RestaurantTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
    @Column(name = "TAG_ID")
    private Integer tagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", nullable = false, length = 30)
    private CategoryType category;

    @Column(name = "CUSTOM_TAG", length = 100)
    private String customTag;

    // 이 태그를 참조하는 식당들
    @OneToMany(mappedBy = "restaurantTag")
    private List<Restaurant> restaurants = new ArrayList<>();
}
