package com.eatproject.backend.blog.dto;

import com.eatproject.backend.blog.entity.BlogPost;
import lombok.Getter;

import java.util.List;

/**
 * 프론트엔드 BlogPost 인터페이스와 필드명 완전 일치:
 *
 * export interface BlogPost {
 *   id: number; restaurant: string; category: string; area: string
 *   title: string; content: string; rating: number; photos: string[]
 *   tags: string[]; author: string; authorColor: string;
 *   date: string; likes: number; liked?: boolean
 * }
 */
@Getter
public class BlogPostResponseDto {

    private final Long id;               // postId → id
    private final String restaurant;
    private final String category;
    private final String area;
    private final String title;
    private final String content;
    private final int rating;
    private final List<String> photos;
    private final List<String> tags;
    private final String author;         // authorName
    private final String authorColor;
    private final String date;           // "yyyy.MM.dd" 포맷
    private final int likes;
    private final boolean liked;         // 현재 요청자의 좋아요 여부

    public BlogPostResponseDto(BlogPost post, boolean liked) {
        this.id          = post.getPostId();
        this.restaurant  = post.getRestaurant();
        this.category    = post.getCategory();
        this.area        = post.getArea() != null ? post.getArea() : "";
        this.title       = post.getTitle();
        this.content     = post.getContent();
        this.rating      = post.getRating();
        this.photos      = post.getPhotos();
        this.tags        = post.getTags();
        this.author      = post.getAuthorName();
        this.authorColor = post.getAuthorColor();
        // createdAt → "yyyy.MM.dd" 문자열 변환
        this.date        = post.getCreatedAt() != null
                ? post.getCreatedAt().toLocalDate().toString().replace("-", ".")
                : "";
        this.likes       = post.getLikes();
        this.liked       = liked;
    }

    /** liked 없이 생성 (비인증 요청용) */
    public BlogPostResponseDto(BlogPost post) {
        this(post, false);
    }
}
