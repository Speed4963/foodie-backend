package com.eatproject.backend.posts.dto;

import com.eatproject.backend.posts.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private final Long postId;
    private final Integer boardId;
    private final Long parentId;
    private final String writer; // 익명 처리 로직 반영 가능
    private final String content;
    private final Integer replyCount;
    private final Integer likeCount;
    private final String imgUrl;
    private final String thumbUrl;
    private final LocalDateTime bumpAt;
    private final LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.boardId = post.getBoard().getBoardId();
        this.parentId = post.getParentId();
        this.content = post.getContent();
        this.replyCount = post.getReplyCount();
        this.likeCount = post.getLikeCount();
        this.imgUrl = post.getImgUrl();
        this.thumbUrl = post.getThumbUrl();
        this.bumpAt = post.getBumpAt();
        this.createdAt = post.getCreatedAt();

        // 익명 여부에 따른 작성자명 마스킹 처리
        this.writer = Boolean.TRUE.equals(post.getIsAnonymous()) ? "익명" : post.getWriter();
    }
}