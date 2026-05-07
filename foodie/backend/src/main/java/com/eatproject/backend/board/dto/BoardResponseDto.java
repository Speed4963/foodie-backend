package com.eatproject.backend.board.dto;

import com.eatproject.backend.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private final Integer boardId;
    private final String name;
    private final String slug;
    private final Integer generation;
    private final String status;
    private final Integer postCount;

    public BoardResponseDto(Board board) {
        this.boardId = board.getBoardId();
        this.name = board.getName();
        this.slug = board.getSlug();
        this.generation = board.getGeneration();
        this.status = board.getStatus();
        this.postCount = board.getPostCount();
    }
}