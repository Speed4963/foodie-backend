package com.eatproject.backend.board.entity;

import com.eatproject.backend.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOARDS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "DELETED_AT IS NULL")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Integer boardId;

    @Column(name = "PARENT_BOARD_ID")
    private Integer parentBoardId;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "GENERATION", nullable = false)
    private Short generation = 1; // SQL SMALLINT 대응

    @Column(name = "SLUG", length = 50, nullable = false, unique = true)
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

    // 비즈니스 로직: 상태 변경 시 시간 기록 자동화
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.statusChangedAt = LocalDateTime.now();
    }

    // 게시글 수 증가 (세대 분기 트리거용)
    public void incrementPostCount() {
        this.postCount++;
    }
}