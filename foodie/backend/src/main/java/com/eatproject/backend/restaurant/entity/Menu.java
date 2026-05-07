package com.eatproject.backend.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENUS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "menu_seq",
        sequenceName = "MENU_SEQ",
        allocationSize = 50 // 메뉴도 여러 개씩 저장되므로 50 정도가 적당합니다.
)
@ToString(exclude = "restaurant") // 무한 루프 방지
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_seq")
    @Column(name = "MENU_ID")
    private Integer menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REST_ID", nullable = false)
    private Restaurant restaurant;

    @Column(name = "P_NAME", nullable = false, length = 200)
    private String pName;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Column(name = "IS_REPRESENTATIVE", nullable = false)
    private Boolean isRepresentative;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}