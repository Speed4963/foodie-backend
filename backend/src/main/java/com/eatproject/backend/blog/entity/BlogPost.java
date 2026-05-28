package com.eatproject.backend.blog.entity;

import com.eatproject.backend.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 맛집 리뷰 블로그 게시글 엔티티
 *
 * 기존 posts.entity.Post (커뮤니티 스레드)와 완전히 분리된 별도 테이블.
 * 프론트엔드 BlogPost 인터페이스 필드와 1:1 대응:
 *   id, restaurant, category, area, title, content,
 *   rating, photos, tags, author, authorColor, date, likes, liked
 *
 * DB 테이블: BLOG_POSTS  (기존 POSTS 테이블을 건드리지 않음)
 */
@Entity
@Table(name = "BLOG_POSTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BlogPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long postId;

    // ── 작성자 (JWT subject = email 또는 nickname) ────────────────
    @Column(name = "AUTHOR_ID", nullable = false, length = 255)
    private String authorId;          // member PK 또는 email

    @Column(name = "AUTHOR_NAME", nullable = false, length = 50)
    private String authorName;        // 표시용 닉네임

    @Column(name = "AUTHOR_COLOR", length = 20)
    @Builder.Default
    private String authorColor = "#E8272A";

    // ── 리뷰 핵심 필드 ──────────────────────────────────────────
    @Column(name = "RESTAURANT", nullable = false, length = 100)
    private String restaurant;

    @Column(name = "CATEGORY", nullable = false, length = 50)
    private String category;

    @Column(name = "AREA", length = 50)
    private String area;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "RATING", nullable = false)
    @Builder.Default
    private Integer rating = 3;       // 1~5

    // ── 사진 / 태그 (쉼표 구분 문자열 → List 변환은 @Transient 편의 메서드로) ──
    @Lob
    @Column(name = "PHOTOS")
    private String photosRaw;         // "url1,url2,url3"

    @Column(name = "TAGS", length = 500)
    private String tagsRaw;           // "태그1,태그2"

    // ── 좋아요 수 ────────────────────────────────────────────────
    @Column(name = "LIKES", nullable = false)
    @Builder.Default
    private Integer likes = 0;

    // ── List ↔ raw 변환 편의 메서드 ──────────────────────────────

    @Transient
    public List<String> getPhotos() {
        if (photosRaw == null || photosRaw.isBlank()) return List.of();
        return List.of(photosRaw.split(","));
    }

    public void setPhotos(List<String> photos) {
        this.photosRaw = (photos == null || photos.isEmpty()) ? "" : String.join(",", photos);
    }

    @Transient
    public List<String> getTags() {
        if (tagsRaw == null || tagsRaw.isBlank()) return List.of();
        return List.of(tagsRaw.split(","));
    }

    public void setTags(List<String> tags) {
        this.tagsRaw = (tags == null || tags.isEmpty()) ? "" : String.join(",", tags);
    }

    // ── 비즈니스 로직 ────────────────────────────────────────────

    /** 게시글 내용 전체 수정 (PUT) */
    public void update(String restaurant, String category, String area,
                       String title, String content, int rating,
                       List<String> photos, List<String> tags) {
        this.restaurant = restaurant;
        this.category   = category;
        this.area       = area;
        this.title      = title;
        this.content    = content;
        this.rating     = rating;
        setPhotos(photos);
        setTags(tags);
    }

    /** 좋아요 +1 */
    public void incrementLikes() { this.likes++; }

    /** 좋아요 -1 (0 미만 방지) */
    public void decrementLikes() { if (this.likes > 0) this.likes--; }
}
