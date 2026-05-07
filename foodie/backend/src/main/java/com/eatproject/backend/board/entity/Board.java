package com.eatproject.backend.board.entity;


import com.eatproject.backend.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARDS")
@Getter
@Setter // 값 변경(status 등)을 위해 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "DELETED_AT IS NULL")
public class Board extends BaseTimeEntity { // 1. BaseTimeEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Integer boardId;

    @Column(name = "PARENT_BOARD_ID")
    private Integer parentBoardId;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    // 2. @Builder.Default를 사용하여 초기값 설정 (Lombok 어노테이션 사용)
    @Builder.Default
    @Column(name = "GENERATION", nullable = false)
    private Integer generation = 1;

    @Column(name = "SLUG", length = 50, nullable = false)
    private String slug;

    @Builder.Default
    @Column(name = "STATUS", length = 20, nullable = false)
    private String status = "PROPOSED";

    @Builder.Default
    @Column(name = "POST_COUNT", nullable = false)
    private Integer postCount = 0;

    @Column(name = "PROPOSED_BY", length = 255, nullable = false)
    private String proposedBy;

    @Column(name = "STATUS_CHANGED_AT")
    private LocalDateTime statusChangedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}