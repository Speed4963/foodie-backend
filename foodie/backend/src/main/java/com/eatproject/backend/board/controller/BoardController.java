package com.eatproject.backend.board.controller;

import com.eatproject.backend.board.dto.BoardResponseDto;
import com.eatproject.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 전체 활성 게시판 목록 API
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getActiveBoards());
    }

    // 특정 게시판 상세 조회 API (슬러그 기준)
    @GetMapping("/{slug}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable String slug) {
        return ResponseEntity.ok(boardService.getBoardBySlug(slug));
    }
}