package com.eatproject.backend.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN_LOGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "ADMIN_EMAIL", nullable = false)
    private String adminEmail;

    @Column(name = "ACTION_TYPE", length = 30, nullable = false)
    private String actionType;

    @Column(name = "BANNED_USER")
    private String bannedUser;

    @Column(name = "TARGET_POST")
    private Long targetPost;

    @Column(name = "APPROVED_BOARD")
    private Integer approvedBoard;

    @Column(name = "REASON", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Builder.Default
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}