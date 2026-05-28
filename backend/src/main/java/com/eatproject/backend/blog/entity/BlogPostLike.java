package com.eatproject.backend.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 블로그 게시글 좋아요 기록 테이블
 * (사용자 1명이 같은 글에 중복 좋아요 불가)
 */
@Entity
@Table(name = "BLOG_POST_LIKES",
       uniqueConstraints = @UniqueConstraint(columnNames = {"POST_ID", "LIKER_ID"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BlogPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private BlogPost post;

    /** JWT subject (email or memberId) */
    @Column(name = "LIKER_ID", nullable = false, length = 255)
    private String likerId;
}
