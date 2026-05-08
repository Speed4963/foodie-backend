package com.eatproject.backend.posts.entity;

import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Setter
@Entity
@Table(name = "POSTS")
@Getter
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
    private Long parentId; // NULL이면 스레드 원문(OP), 값이 있으면 답글

    @Column(name = "QUOTE_ID")
    private Long quoteId; // >>12345 인용 기능용

    @Builder.Default
    @Column(name = "DEPTH", nullable = false)
    private Integer depth = 0; // 0: OP, 1: Reply (체크 제약조건)

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
    private String preview; // JSON/TEXT 캐시용

    @Column(name = "BUMP_AT", nullable = false)
    private LocalDateTime bumpAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // 엔티티가 처음 저장될 때 BUMP_AT 초기화
    @PrePersist
    public void prePersist() {
        if (this.bumpAt == null) {
            this.bumpAt = LocalDateTime.now();
        }
    }

    // 비즈니스 로직: 새 답글이 달렸을 때 호출 (스레드 끌어올리기 및 카운트 증가)
    public void updateOnNewReply() {
        this.replyCount++;
        this.bumpAt = LocalDateTime.now();
    }

    // 비즈니스 로직: 스레드 잠금 처리
    public void lockThread() {
        this.isLocked = true;
        this.lockedAt = LocalDateTime.now();
    }
}