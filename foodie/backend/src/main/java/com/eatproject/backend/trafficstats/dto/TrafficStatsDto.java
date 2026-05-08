package com.eatproject.backend.trafficstats.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficStatsDto {
    private String boardName;    // 게시판 이름
    private String keyword;      // 키워드
    private Long mentionCount;   // 언급 횟수
    private LocalDate statDate;  // 통계 날짜
}