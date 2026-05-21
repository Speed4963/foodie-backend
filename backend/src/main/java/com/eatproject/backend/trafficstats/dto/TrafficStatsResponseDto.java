package com.eatproject.backend.trafficstats.dto;

import lombok.*;
import java.time.LocalDate;
// dto를 나눈 이유는 여기선 테이블에 없는 정보를 다루기 때문
@Getter
@NoArgsConstructor
@Builder
public class TrafficStatsResponseDto {
    private String boardName;
    private String keyword;
    private Long mentionCount;
    private LocalDate startDate;
    private LocalDate endDate;

    // JPQL에서 명확하게 매핑하기 위해 수동 생성자를 유지하는 것이 안전합니다.
    public TrafficStatsResponseDto(String boardName, String keyword, Long mentionCount, LocalDate startDate, LocalDate endDate) {
        this.boardName = boardName;
        this.keyword = keyword;
        this.mentionCount = mentionCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}