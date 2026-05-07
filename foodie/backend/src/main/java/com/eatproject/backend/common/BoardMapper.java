package com.eatproject.backend.common;

import com.eatproject.backend.board.dto.BoardResponseDto;
// Board 엔티티의 정확한 패키지 경로를 확인해서 import 하세요
import com.foodie.domain.board.entity.Board;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BoardMapper {

    // 1. Entity -> DTO 변환
    BoardResponseDto toDto(Board board);

    // 2. DTO -> Entity (필요할 경우를 대비해 추가)
    Board toEntity(BoardResponseDto dto);

    // 3. 수정용 매핑 (DTO의 내용을 기존 Entity에 덮어쓰기)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "boardId", ignore = true)     // PK는 수정 금지
    @Mapping(target = "createdAt", ignore = true)   // 생성일은 수정 금지
    void updateFromDto(BoardResponseDto dto, @MappingTarget Board entity);
}