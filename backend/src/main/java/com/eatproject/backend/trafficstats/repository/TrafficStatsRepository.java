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

    // 2. 배치 재실행 시 중복 방지를 위한 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TrafficStats ts WHERE ts.statDate = :date")
    void deleteByStatDate(@Param("date") LocalDate date);

    @Query("SELECT ts FROM TrafficStats ts WHERE ts.statDate = :statDate")
    List<TrafficStats> findAllByStatDate(@Param("statDate") LocalDate statDate);



}
