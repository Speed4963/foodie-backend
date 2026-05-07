package com.eatproject.backend.posts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private Integer boardId;
    private Long parentId; // 스레드 원문 작성시 null, 답글 작성시 해당 스레드의 ID
    private Long quoteId;
    private String writer; // 추후 Security 적용 시 토큰에서 추출
    private String content;
    private Boolean isAnonymous;
    private String imgUrl;
    private String thumbUrl;
}