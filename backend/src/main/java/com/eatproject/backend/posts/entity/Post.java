package com.eatproject.backend.posts.entity;

import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "POSTS")
@Getter
@Setter // 서비스 계층의 편의를 위해 유지 (혹은 필요한 필드에만 개별 적용 권장)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "DELETED_AT IS NULL")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "QUOTE_ID")
    private Long quoteId;

    @Builder.Default
    @Column(name = "DEPTH", nullable = false)
    private Integer depth = 0;

    @Column(name = "WRITER", length = 255, nullable = false)
    private String writer;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "IS_ANONYMOUS")
    private Boolean isAnonymous = false;

    @Builder.Default
    @Column(name = "REPLY_COUNT", nullable = false)
    private Integer replyCount = 0;

    @Builder.Default
    @Column(name = "LIKE_COUNT", nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @Column(name = "IS_LOCKED", nullable = false)
    private Boolean isLocked = false;

    @Column(name = "LOCKED_AT")
    private LocalDateTime lockedAt;

    @Column(name = "IMG_URL", length = 512)
    private String imgUrl;

    @Column(name = "THUMB_URL", length = 512)
    private String thumbUrl;

    @Lob
    @Column(name = "PREVIEW")
    private String preview;

    @Column(name = "BUMP_AT", nullable = false)
    private LocalDateTime bumpAt;

    // [삭제] @Column(name = "DELETED_AT") private LocalDateTime deletedAt;
    // 이유: BaseTimeEntity 상속을 통해 이미 포함되어 있음
    @PrePersist
    public void prePersist() {
        if (this.bumpAt == null) {
            this.bumpAt = LocalDateTime.now();
        }
    }

    // --- 비즈니스 로직 ---
    /**
     * 스레드 잠금 처리 (PostService에서 호출)
     */
    public void lockThread() {
        this.isLocked = true;
        this.lockedAt = LocalDateTime.now();
    }

    /**
     * 새 답글 등록 시 호출 (카운트 증가 및 스레드 상단 노출용 시각 갱신)
     */
    public void updateOnNewReply() {
        this.replyCount++;
        this.bumpAt = LocalDateTime.now();
    }
}