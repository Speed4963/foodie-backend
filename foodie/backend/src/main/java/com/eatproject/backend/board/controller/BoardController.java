package com.eatproject.backend.board.controller;

import com.eatproject.backend.board.dto.BoardResponseDto;
import com.eatproject.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getActiveBoards());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable String slug) {
        return ResponseEntity.ok(boardService.getBoardBySlug(slug));
    }
}