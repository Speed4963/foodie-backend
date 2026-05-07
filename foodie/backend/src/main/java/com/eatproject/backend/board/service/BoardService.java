package com.eatproject.backend.board.service;

import com.eatproject.backend.board.dto.BoardResponseDto;
import com.eatproject.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    // 활성화된 모든 게시판 목록 조회
    public List<BoardResponseDto> getActiveBoards() {
        return boardRepository.findAllByStatus("ACTIVE").stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    // 슬러그(예: gangnam-food)로 특정 게시판 조회
    public BoardResponseDto getBoardBySlug(String slug) {
        return boardRepository.findBySlug(slug)
                .map(BoardResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판을 찾을 수 없습니다: " + slug));
    }
}