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


    /**
     * 1. 전체 통계 조회
     * URL: GET /api/admin/traffic-stats/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<TrafficStatsDto>> getAllStats() {
        return ResponseEntity.ok(trafficStatsService.getAllStats());
    }

    /**
     * 2. 날짜별 전체 통계 조회
     * URL: GET /api/admin/traffic-stats/date?date=2026-05-28
     */
    @GetMapping("/date")
    public ResponseEntity<List<TrafficStatsDto>> getAllStatsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(trafficStatsService.getAllStatsByDate(date));
    }
}