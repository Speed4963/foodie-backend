package com.eatproject.backend.posts.repository;

import com.eatproject.backend.posts.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 특정 게시판의 원문(OP) 목록 조회 (BUMP순)
    // @Where에 의해 DELETED_AT IS NULL 조건이 자동으로 붙습니다.
    Page<Post> findAllByBoard_BoardIdAndDepthOrderByBumpAtDesc(Integer boardId, Integer depth, Pageable pageable);

    // 2. 특정 원문에 달린 답글 목록 조회 (작성순)
    List<Post> findAllByParentIdOrderByCreatedAtAsc(Long parentId);

    // [추가] 3. 특정 유저(WRITER)가 쓴 글 목록 조회 (마이페이지용)
    Page<Post> findAllByWriter(String writer, Pageable pageable);

    // [추가] 4. 특정 글(QUOTE_ID)을 인용하고 있는 글들 찾기
    List<Post> findAllByQuoteId(Long quoteId);

    // [추가] 5. 게시판 내 키워드 검색 (간단한 버전)
    Page<Post> findAllByBoard_BoardIdAndContentContaining(Integer boardId, String content, Pageable pageable);

//    트래픽집계용로직
    List<Post> findAllByBoard_BoardIdAndCreatedAtBetween(
            Integer boardId,
            LocalDateTime start,
            LocalDateTime end
    );

}