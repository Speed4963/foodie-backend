package com.eatproject.backend.trafficstats.controller;

import com.eatproject.backend.trafficstats.dto.TrafficStatsDto;
import com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto;
import com.eatproject.backend.trafficstats.service.TrafficStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/traffic-stats")
@RequiredArgsConstructor
public class TrafficStatsController {

    private final TrafficStatsService trafficStatsService;


//      수동 배치 실행 API

    @PostMapping("/run")
    public ResponseEntity<String> runManualBatch(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now().minusDays(1);
        trafficStatsService.generateAndSaveStats(targetDate);
        return ResponseEntity.ok(targetDate + " 집계 완료");
    }


//      특정 날짜 통계 조회 (TrafficStatsDto 사용)
//      GET /api/admin/traffic-stats?boardId=1&date=2026-05-08

    @GetMapping
    public ResponseEntity<List<TrafficStatsDto>> getStats(
            @RequestParam Integer boardId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(trafficStatsService.getStatsForAdmin(boardId, date));
    }


//      기간별 통계 조회 (TrafficStatsResponseDto 사용)
//      GET /api/admin/traffic-stats/period?boardId=1&startDate=2026-05-08&endDate=2026-05-10

    @GetMapping("/period")
    public ResponseEntity<List<TrafficStatsResponseDto>> getPeriodStats(
            @RequestParam Integer boardId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(trafficStatsService.getStatsByPeriod(boardId, startDate, endDate));
    }
}