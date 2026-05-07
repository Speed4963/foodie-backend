package com.eatproject.backend.posts.repository;

import com.eatproject.backend.posts.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 특정 게시판의 원문(OP, Depth=0) 스레드 목록을 BUMP_AT 내림차순으로 페이징 조회
    Page<Post> findAllByBoard_BoardIdAndDepthOrderByBumpAtDesc(Integer boardId, Integer depth, Pageable pageable);

    // 2. 특정 원문(OP)에 달린 답글(Depth=1) 목록 조회 (오래된 순)
    List<Post> findAllByParentIdOrderByCreatedAtAsc(Long parentId);
}