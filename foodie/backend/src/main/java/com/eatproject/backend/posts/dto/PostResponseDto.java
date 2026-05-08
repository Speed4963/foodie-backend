package com.eatproject.backend.posts.dto;

import com.eatproject.backend.posts.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private final Long postId;
    private final Integer boardId;
    private final Long parentId;
    private final Long quoteId;      // [추가] 인용된 글 번호 (>>12345 표시용)
    private final String writer;
    private final String content;
    private final Integer replyCount;
    private final Integer likeCount;
    private final String imgUrl;
    private final String thumbUrl;
    private final Boolean isLocked;   // [추가] 잠금 상태 (프론트에서 댓글창 비활성화 여부 결정)
    private final LocalDateTime lockedAt; // [추가] 잠긴 시간
    private final LocalDateTime bumpAt;
    private final LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.boardId = post.getBoard().getBoardId();
        this.parentId = post.getParentId();
        this.quoteId = post.getQuoteId(); // [추가]
        this.content = post.getContent();
        this.replyCount = post.getReplyCount();
        this.likeCount = post.getLikeCount();
        this.imgUrl = post.getImgUrl();
        this.thumbUrl = post.getThumbUrl();
        this.isLocked = post.getIsLocked(); // [추가]
        this.lockedAt = post.getLockedAt(); // [추가]
        this.bumpAt = post.getBumpAt();
        this.createdAt = post.getCreatedAt();

        // 익명 여부에 따른 작성자명 마스킹 처리
        this.writer = Boolean.TRUE.equals(post.getIsAnonymous()) ? "익명" : post.getWriter();
    }
}