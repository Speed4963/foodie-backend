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
public class Board extends BaseTimeEntity { // BaseTimeEntity 상속

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
    private Short generation = 1;

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

    // --- 비즈니스 로직 ---
    /**
     * 상태 변경 시 시간 기록 자동화
     * 수동으로 제어하여 비즈니스 의미가 있는 상태 변화만 기록함
     */
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.statusChangedAt = LocalDateTime.now();
    }

    /**
     * 블루리본 매거진처럼 에디터 전용 게시판인지 확인
     */
    public boolean isBlogType() {
        return "blog".equals(this.slug) || "magazine".equals(this.slug);
    }

    /**
     * 게시글 수 증가 (세대 분기 트리거용)
     */
    public void incrementPostCount() {
        if (this.postCount == null) this.postCount = 0;
        this.postCount++;
    }
}