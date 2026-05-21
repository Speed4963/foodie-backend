package com.eatproject.backend.trafficstats.repository;

import com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto;
import com.eatproject.backend.trafficstats.entity.TrafficStats;
import com.eatproject.backend.trafficstats.dto.TrafficStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrafficStatsRepository extends JpaRepository<TrafficStats, Long> {

    // 1. 단일 날짜 및 게시판 기준 내림차순 조회
    @Query("SELECT ts FROM TrafficStats ts " +
            "JOIN FETCH ts.board " +
            "WHERE ts.board.boardId = :boardId AND ts.statDate = :statDate " +
            "ORDER BY ts.mentionCount DESC")
    List<TrafficStats> findAllByBoardIdAndStatDate(
            @Param("boardId") Integer boardId,
            @Param("statDate") LocalDate statDate);

    // 2. 배치 재실행 시 중복 방지를 위한 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TrafficStats ts WHERE ts.statDate = :date")
    void deleteByStatDate(@Param("date") LocalDate date);

    // 3. 기간별 합계 조회 (SUM)
    @Query("SELECT new com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto(" +
            "ts.board.name, " +
            "ts.keyword, " +
            "SUM(ts.mentionCount), " +
            ":startDate, " +
            ":endDate) " +
            "FROM TrafficStats ts " +
            "WHERE ts.board.boardId = :boardId " +
            "AND ts.statDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ts.board.name, ts.keyword " +
            "ORDER BY SUM(ts.mentionCount) DESC")
    List<TrafficStatsResponseDto> findStatsByPeriod(
            @Param("boardId") Integer boardId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
