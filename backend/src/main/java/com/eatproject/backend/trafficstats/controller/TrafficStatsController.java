package com.eatproject.backend.trafficstats.controller;

import com.eatproject.backend.trafficstats.dto.TrafficStatsDto;
import com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto;
import com.eatproject.backend.trafficstats.service.TrafficStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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


    @GetMapping("/all")
    public ResponseEntity<Page<TrafficStatsDto>> getAllStats(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(trafficStatsService.findAll(page, size));
    }

    @GetMapping("/date")
    public ResponseEntity<Page<TrafficStatsDto>> getAllStatsByDate(@RequestParam LocalDate date, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(trafficStatsService.getAllStatsByDatePaged(date, page, size));
    }
}