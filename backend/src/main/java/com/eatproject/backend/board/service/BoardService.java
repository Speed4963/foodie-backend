package com.eatproject.backend.board.service;

import com.eatproject.backend.board.dto.BoardResponseDto;
import com.eatproject.backend.board.entity.Board;
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

    // 활성화된 게시판 목록만 조회
    public List<BoardResponseDto> getActiveBoards() {
        return boardRepository.findAllByStatus("ACTIVE").stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    // 슬러그 기준 상세 조회
    public BoardResponseDto getBoardBySlug(String slug) {
        Board board = boardRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다: " + slug));
        return new BoardResponseDto(board);
    }
}