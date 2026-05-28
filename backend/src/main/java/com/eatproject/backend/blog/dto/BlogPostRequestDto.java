package com.eatproject.backend.blog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 프론트엔드 api.createPost / api.updatePost 에서 전송하는 body
 *
 * {
 *   restaurant, category, area, title, content,
 *   rating, photos: string[], tags: string[],
 *   authorId, author, authorColor   (createPost 시에만 포함)
 * }
 */
@Getter
@NoArgsConstructor
public class BlogPostRequestDto {

    @NotBlank(message = "식당 이름은 필수입니다")
    private String restaurant;

    private String category;
    private String area;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Min(1) @Max(5)
    private int rating;

    private List<String> photos;
    private List<String> tags;

    // createPost 시 프론트가 함께 전송하는 작성자 정보
    // (추후 JWT Security 적용 시 토큰에서 추출하도록 변경)
    private String authorId;       // email 또는 member PK
    private String author;         // 닉네임 (표시명)
    private String authorColor;    // 아바타 색상
}
