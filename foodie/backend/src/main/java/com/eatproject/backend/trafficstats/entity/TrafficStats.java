package com.eatproject.backend.trafficstats.entity;

import com.eatproject.backend.board.entity.Board;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TRAFFIC_STATS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrafficStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STAT_ID")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;

    @Column(name = "KEYWORD", nullable = false)
    private String keyword;

    @Column(name = "MENTION_COUNT", nullable = false)
    private Integer mentionCount;

    @Column(name = "STAT_DATE", nullable = false)
    private LocalDate statDate;
}