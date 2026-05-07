package com.eatproject.backend.board.repository;

import com.foodie.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 특정 슬러그(URL 식별자)로 게시판 찾기
    Optional<Board> findBySlug(String slug);

    // 활성화된 게시판 목록만 가져오기
    List<Board> findAllByStatus(String status);
}