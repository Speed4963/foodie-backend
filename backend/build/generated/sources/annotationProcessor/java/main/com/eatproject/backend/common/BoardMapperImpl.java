package com.eatproject.backend.common;

import com.eatproject.backend.board.dto.BoardResponseDto;
import com.eatproject.backend.board.entity.Board;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-28T17:08:26+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.4.jar, environment: Java 17.0.18 (Azul Systems, Inc.)"
)
@Component
public class BoardMapperImpl implements BoardMapper {

    @Override
    public BoardResponseDto toDto(Board board) {
        if ( board == null ) {
            return null;
        }

        Board board1 = null;

        board1 = board;

        BoardResponseDto boardResponseDto = new BoardResponseDto( board1 );

        return boardResponseDto;
    }

    @Override
    public Board toEntity(BoardResponseDto dto) {
        if ( dto == null ) {
            return null;
        }

        Board.BoardBuilder board = Board.builder();

        board.boardId( dto.getBoardId() );
        board.name( dto.getName() );
        board.generation( dto.getGeneration() );
        board.slug( dto.getSlug() );
        board.status( dto.getStatus() );
        board.postCount( dto.getPostCount() );

        return board.build();
    }

    @Override
    public void updateFromDto(BoardResponseDto dto, Board entity) {
        if ( dto == null ) {
            return;
        }
    }
}
