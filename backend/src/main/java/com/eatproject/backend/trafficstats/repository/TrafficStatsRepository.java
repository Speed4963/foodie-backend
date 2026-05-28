package com.eatproject.backend.trafficstats.repository;

import com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto;
import com.eatproject.backend.trafficstats.entity.TrafficStats;
import com.eatproject.backend.trafficstats.dto.TrafficStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 2. 배치 재실행 시 중복 방지를 위한 삭제
    @Modifying(clearAutomatically = true)
    // 🌟 1단계: 날짜별 조회 쿼리에 Pageable과 Page 적용
    @Query("SELECT ts FROM TrafficStats ts WHERE ts.statDate = :statDate")
    Page<TrafficStats> findAllByStatDate(@Param("statDate") LocalDate statDate, Pageable pageable);

    // 전체 조회 페이징 (기존 기본 메서드 오버라이딩)
    Page<TrafficStats> findAll(Pageable pageable);



}
