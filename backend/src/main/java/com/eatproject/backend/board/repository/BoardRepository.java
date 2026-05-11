package com.eatproject.backend.board.repository;

import com.eatproject.backend.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 슬러그 기준 조회
    Optional<Board> findBySlug(String slug);

    // 특정 상태(예: ACTIVE)의 게시판만 조회
    List<Board> findAllByStatus(String status);
}