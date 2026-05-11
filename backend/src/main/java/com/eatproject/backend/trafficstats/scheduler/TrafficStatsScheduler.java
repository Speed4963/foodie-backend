package com.eatproject.backend.trafficstats.scheduler;

import com.eatproject.backend.trafficstats.service.TrafficStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TrafficStatsScheduler {

    private final TrafficStatsService trafficStatsService;
//매일 아침 9시에 전날 키워드 집계
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runDailyBatch() {
        trafficStatsService.generateAndSaveStats(LocalDate.now().minusDays(1));
    }
}